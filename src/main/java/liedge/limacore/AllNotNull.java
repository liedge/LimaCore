package liedge.limacore;

import org.jetbrains.annotations.NotNull;

import javax.annotation.meta.TypeQualifierDefault;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@NotNull
@Retention(RetentionPolicy.RUNTIME)
@TypeQualifierDefault({ElementType.METHOD, ElementType.PARAMETER})
public @interface AllNotNull {}