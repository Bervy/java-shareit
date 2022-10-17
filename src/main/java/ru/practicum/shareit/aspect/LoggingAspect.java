package ru.practicum.shareit.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Aspect
@Slf4j
@Component
public class LoggingAspect {

    @Pointcut(value = "execution(* ru.practicum.shareit.*.controller.*.*(..))")
    public void controllerPointcut() {

    }

    @Pointcut(value = "execution(* ru.practicum.shareit.*.*.*.*(..))")
    public void allPointcut() {

    }

    @Before("controllerPointcut()")
    public void logBeforeMethodsAdvice(JoinPoint joinPoint) {
        String methodName = joinPoint.getSignature().getName();
        String className = joinPoint.getTarget().getClass().toString();
        Object[] array = joinPoint.getArgs();
        log.info("Request received for " + className + " : " + methodName + "()" + "arguments : "
                + Arrays.toString(array));
    }

    @After("controllerPointcut()")
    public void logAfterMethodsAdvice(JoinPoint joinPoint) {
        String methodName = joinPoint.getSignature().getName();
        String className = joinPoint.getTarget().getClass().toString();
        log.info("Successful execution of the method " + className + " : " + methodName + "()");
    }

    @AfterThrowing(value = "allPointcut()", throwing = "ex")
    public void afterThrowingAdvice(JoinPoint joinPoint, Exception ex) {
        log.info("After throwing exception in method : " + joinPoint.getSignature());
        log.info("Exception is  : " + ex.getMessage());
    }
}