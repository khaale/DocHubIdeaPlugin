package org.dochub.idea.arch.references.providers.imports;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.TextRange;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.patterns.ElementPattern;
import com.intellij.patterns.PlatformPatterns;
import com.intellij.psi.*;
import com.intellij.util.ProcessingContext;
import org.dochub.idea.arch.references.providers.BaseReferencesProvider;
import org.dochub.idea.arch.utils.PsiUtils;
import org.dochub.idea.arch.utils.VirtualFileSystemUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.yaml.psi.YAMLDocument;
import org.jetbrains.yaml.psi.YAMLKeyValue;
import org.jetbrains.yaml.psi.YAMLSequenceItem;

public class ReferencesProvider extends BaseReferencesProvider {
    private static String keyword = "imports";

    private class FileSourceReference extends PsiReferenceBase {

        PsiFile source;

        public FileSourceReference(@NotNull PsiElement element, PsiFile source) {
            super(element);
            this.source = source;
        }

        @Override
        public @NotNull TextRange getAbsoluteRange() {
            return super.getAbsoluteRange();
        }

        @Override
        public @Nullable PsiElement resolve() {
            return source;
        }

        @Override
        public Object @NotNull [] getVariants() {
            return super.getVariants();
        }
    }

    @Override
    public ElementPattern<? extends PsiElement> getPattern() {
        return PlatformPatterns.psiElement()
                .withParent(psi(YAMLSequenceItem.class)
                        .withSuperParent(2,
                                psi(YAMLKeyValue.class)
                                        .withName(PlatformPatterns.string().equalTo(keyword))
                                        .withSuperParent(2, psi(YAMLDocument.class))
                        )
                );
    }

    @Override
    public PsiReference @NotNull [] getReferencesByElement(@NotNull PsiElement element,
                                                           @NotNull ProcessingContext context) {

        Project project = element.getManager().getProject();
//        String currentDir = element.getContainingFile().getParent().findFile(;
//        VirtualFile vTargetFile = VirtualFileSystemUtils.findFile(
//                currentDir + "/" + PsiUtils.getText(element),
//                project
//        );

//        PsiFile targetFile =
//                vTargetFile != null
//                        ? PsiManager.getInstance(element.getManager().getProject()).findFile(vTargetFile)
//                        : null;


        String ref = PsiUtils.getText(element);
        PsiFile containingFile = element.getContainingFile();
        PsiDirectory currDir = containingFile.getParent();
        if (currDir != null) {
            String dirPath = currDir.getVirtualFile().getCanonicalPath().substring(project.getBasePath().length());
            VirtualFile vTargetFile = VirtualFileSystemUtils.findFile(dirPath + "/" + ref, project);
            if (vTargetFile != null) {
                PsiFile targetFile = PsiManager.getInstance(element.getManager().getProject()).findFile(vTargetFile);
                return new PsiReference[]{new FileSourceReference(element, targetFile)};
            } else
                return PsiReference.EMPTY_ARRAY;
        } else
            return PsiReference.EMPTY_ARRAY;


//        PsiFile targetFile =
//                currDir != null && ref.length() > 0
//                        ? currDir.findFile(ref)
//                        : null;
//
//        if (targetFile != null)
//            return new PsiReference[]{new FileSourceReference(element, targetFile)};
//        else
//            return PsiReference.EMPTY_ARRAY;
    }
}
