package com.alexsobiek.nexus.plugin.asm;

import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.Opcodes;

import java.util.*;
import java.util.function.Predicate;

public class AnnotationFinder extends ClassVisitor {
    private final Predicate<String> filter;
    private Optional<Map<String, Object>> fields;
    private boolean hasAnnotation;


    public AnnotationFinder(Predicate<String> filter) {
        super(Opcodes.ASM8);
        this.filter = filter;
    }

    @Override
    public AnnotationVisitor visitAnnotation(final String descriptor, final boolean visible) {
        if (filter.test(descriptor)) {
            hasAnnotation = true;
            HashMap<String, Object> fields = new HashMap<>();
            this.fields = Optional.of(fields);
            return new AnnotationInspector(fields);
        } else return super.visitAnnotation(descriptor, visible);
    }

    public boolean hasAnnotation() {
        return hasAnnotation;
    }

    public Optional<Map<String, Object>> getFields() {
        if (fields == null) return Optional.empty();
        else return fields;
    }

    static class AnnotationInspector extends AnnotationVisitor {
        private final HashMap<String, Object> fields;

        AnnotationInspector(HashMap<String, Object> fields) {
            super(Opcodes.ASM8);
            this.fields = fields;
        }

        @Override
        public void visit(String name, Object value) {
            fields.put(name, value);
            super.visit(name, value);
        }

        @Override
        public AnnotationVisitor visitArray(String name) {
            return new StringArrayInspector(fields, name);
        }
    }

    static class StringArrayInspector extends AnnotationVisitor {
        private final HashMap<String, Object> fields;
        private final String key;

        protected StringArrayInspector(HashMap<String, Object> fields, String key) {
            super(Opcodes.ASM8);
            this.fields = fields;
            this.key = key;
        }

        @Override
        @SuppressWarnings("unchecked")
        public void visit(String name, Object value) {
            if (fields.containsKey(key)) ((List<String>) fields.get(key)).add((String) value);
            else fields.put(key, new ArrayList<String>() {{
                add((String) value);
            }});
            super.visit(key, value);
        }
    }
}
