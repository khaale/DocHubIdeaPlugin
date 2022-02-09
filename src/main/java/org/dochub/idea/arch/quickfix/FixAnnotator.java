package org.dochub.idea.arch.quickfix;

import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.patterns.ElementPattern;
import com.intellij.psi.PsiElement;
import org.dochub.idea.arch.quickfix.components.ComponentRootStructureFix;
import org.jetbrains.annotations.NotNull;

public class FixAnnotator implements Annotator {

    private BaseQuickFix[] fixes = {
            new ComponentRootStructureFix()
    };

    @Override
    public void annotate(@NotNull PsiElement element, @NotNull AnnotationHolder holder) {
        for (BaseQuickFix fix : fixes) {
            ElementPattern pattern = fix.getFixPattern(element);
            if (pattern.accepts(element)) {
                fix.makeFix(element, holder);
            }
        }
    }
}
