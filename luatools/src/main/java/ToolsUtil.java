import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.psi.PsiElement;
import com.intellij.psi.codeStyle.CodeStyleManager;

public class ToolsUtil {
    public static void reformatJavaFile(PsiElement theElement, Project project) {
        if (theElement == null) {
            return;
        }
        CodeStyleManager codeStyleManager = CodeStyleManager.getInstance(theElement.getProject());
        try {
            codeStyleManager.reformat(theElement);
        } catch (Exception e) {
            Messages.showMessageDialog(project, e.getMessage(), "error", Messages.getInformationIcon());
        }
    }
}
