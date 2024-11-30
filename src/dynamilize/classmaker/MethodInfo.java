package dynamilize.classmaker;

import dynamilize.*;
import dynamilize.classmaker.code.*;

import java.lang.annotation.*;
import java.lang.reflect.*;
import java.util.*;

public class MethodInfo <S, R> extends AnnotatedMember implements IMethod<S, R> {
    private final CodeBlock<R> block;

    IClass<S> owner;

    IClass<R> returnType;
    List<Parameterf<?>> parameterf;
    List<IClass<? extends Throwable>> throwsList;

    boolean initialized;

    public MethodInfo(IClass<S> owner, int modifiers, String name, IClass<R> returnType, IClass<? extends Throwable>[] throwsList, Parameterf<?>... params) {
        super(name);
        IClass<Throwable> thr = ClassInfo.asType(Throwable.class);
        for (IClass<? extends Throwable> iClass : throwsList) {
            if (!thr.isAssignableFrom(iClass))
                throw new IllegalHandleException("throws classes must be extend by java.lang.Throwable, but find " + iClass);
        }

        setModifiers(modifiers);
        this.block = (modifiers & Modifier.ABSTRACT) != 0? null: new CodeBlock<>(this);
        this.owner = owner;
        this.returnType = returnType;
        this.throwsList = Arrays.asList(throwsList);
        this.parameterf = Arrays.asList(params);

        Arrays.stream(params).forEach(e -> e.setOwner(this));

        if (block != null) block.initParams(owner, parameterf);
    }

    @Override
    public List<Parameterf<?>> parameters() {
        return parameterf;
    }

    @Override
    public List<IClass<? extends Throwable>> throwTypes() {
        return throwsList;
    }

    @Override
    public IClass<S> owner() {
        return owner;
    }

    @Override
    public String typeDescription() {
        StringBuilder builder = new StringBuilder();
        builder.append("(");
        for (IClass<?> arg: parameterf.stream().map(Parameterf::getType).toArray(IClass[]::new)) {
            builder.append(arg.realName());
        }

        return builder + ")" + returnType.realName();
    }

    @Override
    public IClass<R> returnType() {
        return returnType;
    }

    @Override
    public CodeBlock<R> block() {
        return block;
    }

    @Override
    public void initAnnotations() {
        if (initialized || name().equals("<clinit>")) return;

        Class<?> clazz = owner().getTypeClass();
        if (clazz == null)
            throw new IllegalHandleException("only get annotation object in existed type info");

        try {
            if (name().equals("<init>")) {
                Constructor<?> cstr = clazz.getDeclaredConstructor(parameters().stream().map(e -> e.getType().getTypeClass()).toArray(Class[]::new));
                for (Annotation annotation: cstr.getAnnotations()) {
                    addAnnotation(new AnnotationDef<>(annotation));
                }
            } else {
                Method met = clazz.getDeclaredMethod(name(), parameters().stream().map(e -> e.getType().getTypeClass()).toArray(Class[]::new));
                for (Annotation annotation: met.getAnnotations()) {
                    addAnnotation(new AnnotationDef<>(annotation));
                }
            }
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }

        initialized = true;
    }

    @Override
    public boolean isType(ElementType type) {
        return type == (Objects.equals(name(), "<init>") ? ElementType.CONSTRUCTOR: ElementType.METHOD);
    }

    @Override
    public <A extends Annotation> A getAnnotation(Class<A> annoClass) {
        Class<?> clazz = owner().getTypeClass();
        if (clazz == null)
            throw new IllegalHandleException("only get annotation object in existed type info");

        try {
            return clazz.getDeclaredMethod(name(), parameters().stream().map(e -> e.getType().getTypeClass()).toArray(Class[]::new)).getAnnotation(annoClass);
        } catch (NoSuchMethodException e) {
            throw new IllegalHandleException(e);
        }
    }
}
