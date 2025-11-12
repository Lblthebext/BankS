package com.sanxiang.deposit.aop;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.sanxiang.deposit.entity.IdempotentRecord;
import com.sanxiang.deposit.mapper.IdempotentRecordMapper;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Slf4j
@Aspect
@Component
public class IdempotentAspect {

    private final IdempotentRecordMapper recordMapper;
    private final ExpressionParser parser = new SpelExpressionParser();

    public IdempotentAspect(IdempotentRecordMapper recordMapper) {
        this.recordMapper = recordMapper;
    }

    @Around("@annotation(idempotent)")
    public Object around(ProceedingJoinPoint joinPoint, Idempotent idempotent) throws Throwable {
        String keyExpression = idempotent.key();
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        String[] parameterNames = signature.getParameterNames();
        Object[] args = joinPoint.getArgs();
        StandardEvaluationContext context = new StandardEvaluationContext();
        for (int i = 0; i < parameterNames.length; i++) {
            context.setVariable(parameterNames[i], args[i]);
        }
        Expression expression = parser.parseExpression(keyExpression);
        String key = expression.getValue(context, String.class);
        IdempotentRecord existing = recordMapper.selectOne(new LambdaQueryWrapper<IdempotentRecord>().eq(IdempotentRecord::getIdempotentId, key));
        if (existing != null) {
            log.info("Idempotent hit for key {}", key);
            return joinPoint.proceed();
        }
        Object result = joinPoint.proceed();
        IdempotentRecord record = new IdempotentRecord();
        record.setIdempotentId(key);
        record.setCreatedAt(LocalDateTime.now());
        recordMapper.insert(record);
        return result;
    }
}
