package org.apache.tools.ant.types;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.ProjectHelper;
import org.apache.tools.ant.Target;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.UnknownElement;
import org.apache.tools.ant.helper.ProjectHelper2;
import org.apache.tools.ant.helper.ProjectHelperImpl;

/* JADX INFO: loaded from: classes3.dex */
public class Description extends DataType {
    public void addText(String str) {
        if (((ProjectHelper) getProject().getReference("ant.projectHelper")) instanceof ProjectHelperImpl) {
            String description = getProject().getDescription();
            if (description == null) {
                getProject().setDescription(str);
            } else {
                getProject().setDescription(description + str);
            }
        }
    }

    public static String getDescription(Project project) {
        List list = (List) project.getReference(ProjectHelper2.REFID_TARGETS);
        if (list == null) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        Iterator it = list.iterator();
        while (it.hasNext()) {
            concatDescriptions(project, (Target) it.next(), sb);
        }
        return sb.toString();
    }

    private static void concatDescriptions(Project project, Target target, StringBuilder sb) {
        String string;
        if (target == null) {
            return;
        }
        for (Task task : findElementInTarget(project, target, "description")) {
            if ((task instanceof UnknownElement) && (string = ((UnknownElement) task).getWrapper().getText().toString()) != null) {
                sb.append(project.replaceProperties(string));
            }
        }
    }

    private static List<Task> findElementInTarget(Project project, Target target, String str) {
        ArrayList arrayList = new ArrayList();
        for (Task task : target.getTasks()) {
            if (str.equals(task.getTaskName())) {
                arrayList.add(task);
            }
        }
        return arrayList;
    }
}
