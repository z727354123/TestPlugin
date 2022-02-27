// Copyright 2000-2022 JetBrains s.r.o. and other contributors. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package org.intellij.sdk.toolWindow;

import com.intellij.analysis.AnalysisScope;
import com.intellij.analysis.JavaAnalysisScope;
import com.intellij.codeInspection.LocalInspectionTool;
import com.intellij.core.CoreProjectScopeBuilder;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.packageDependencies.DependenciesBuilder;
import com.intellij.packageDependencies.FindDependencyUtil;
import com.intellij.packageDependencies.ForwardDependenciesBuilder;
import com.intellij.psi.*;
import com.intellij.psi.impl.JavaPsiFacadeEx;
import com.intellij.psi.impl.source.tree.java.PsiReferenceExpressionImpl;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.search.LocalSearchScope;
import com.intellij.psi.search.ProjectScope;
import com.intellij.psi.search.searches.ReferencesSearch;
import com.intellij.usageView.UsageInfo;
import com.intellij.usages.TextChunk;
import com.intellij.usages.Usage;
import com.intellij.usages.UsageInfo2UsageAdapter;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Set;

public class MyToolWindow {

  private JButton refreshToolWindowButton;
  private JButton hideToolWindowButton;
  private JLabel currentDate;
  private JLabel currentTime;
  private JLabel timeZone;
  private JPanel myToolWindowContent;
  private Project project;
  protected JavaPsiFacadeEx myJavaFacade;

  public MyToolWindow(ToolWindow toolWindow, Project project) {
    this.project = project;
    myJavaFacade = JavaPsiFacadeEx.getInstanceEx(project);
    hideToolWindowButton.addActionListener(e -> toolWindow.hide(null));
    refreshToolWindowButton.addActionListener(e -> currentDateTime());
    refreshToolWindowButton.addActionListener(e -> testE(e));
    this.currentDateTime();
  }

  private void testE(ActionEvent e) {

    final DependenciesBuilder builder = new ForwardDependenciesBuilder(project, new AnalysisScope(project));
    builder.analyze();
    final Set<PsiFile> searchIn = new HashSet<PsiFile>();
    final PsiClass aClass = myJavaFacade.findClass("org.apache.commons.lang3.StringUtils", GlobalSearchScope.allScope(project));
    searchIn.add(aClass.getContainingFile());
    final Set<PsiFile> searchFor = new HashSet<PsiFile>();
    final PsiClass bClass = myJavaFacade.findClass("com.TestClass", GlobalSearchScope.allScope(project));
    searchFor.add(bClass.getContainingFile());

    final UsageInfo[] usagesInfos = FindDependencyUtil.findDependencies(builder, searchIn, searchFor);
    final UsageInfo2UsageAdapter[] usages = UsageInfo2UsageAdapter.convert(usagesInfos);
    final String [] psiUsages = new String [usagesInfos.length];
    for (int i = 0; i < usagesInfos.length; i++) {
      psiUsages[i] = toString(usages[i]);
    }

    // 没啥乱用

    final PsiManager psiManager = PsiManager.getInstance(project);
    final PsiElementFactory elementFactory = JavaPsiFacade.getInstance(psiManager.getProject()).getElementFactory();
    PsiMethod[] isBlanks = aClass.findMethodsByName("isBlank", false);

    GlobalSearchScope projectScope = ProjectScope.getProjectScope(project);
    LocalSearchScope localScope = new LocalSearchScope(bClass);
    final PsiReference[] references =
            ReferencesSearch.search(isBlanks[0], projectScope, false).toArray(new PsiReference[0]);
    PsiReferenceExpressionImpl ref = (PsiReferenceExpressionImpl) references[0];
    ref.accept(new JavaRecursiveElementWalkingVisitor() {
      @Override
      public void visitMethodCallExpression(PsiMethodCallExpression expression) {
        super.visitMethodCallExpression(expression);
        System.out.println("---");
      }
    });
    String out = ref.toString();
    String out2 = ref.getTreeParent().toString();
    System.out.println("---");
  }

  private static String toString(Usage usage) {
    TextChunk[] textChunks = usage.getPresentation().getText();
    StringBuffer result = new StringBuffer();
    for (TextChunk textChunk : textChunks) {
      result.append(textChunk);
    }

    return result.toString();
  }

  public void currentDateTime() {
    // Get current date and time
    Calendar instance = Calendar.getInstance();
    currentDate.setText(
            instance.get(Calendar.DAY_OF_MONTH) + "/"
                    + (instance.get(Calendar.MONTH) + 1) + "/"
                    + instance.get(Calendar.YEAR)
    );
    currentDate.setIcon(new ImageIcon(getClass().getResource("/toolWindow/Calendar-icon.png")));
    int min = instance.get(Calendar.MINUTE);
    String strMin = min < 10 ? "0" + min : String.valueOf(min);
    currentTime.setText(instance.get(Calendar.HOUR_OF_DAY) + ":" + strMin);
    currentTime.setIcon(new ImageIcon(getClass().getResource("/toolWindow/Time-icon.png")));
    // Get time zone
    long gmt_Offset = instance.get(Calendar.ZONE_OFFSET); // offset from GMT in milliseconds
    String str_gmt_Offset = String.valueOf(gmt_Offset / 3600000);
    str_gmt_Offset = (gmt_Offset > 0) ? "GMT + " + str_gmt_Offset : "GMT - " + str_gmt_Offset;
    timeZone.setText(str_gmt_Offset);
    timeZone.setIcon(new ImageIcon(getClass().getResource("/toolWindow/Time-zone-icon.png")));
  }

  public JPanel getContent() {
    return myToolWindowContent;
  }

}
