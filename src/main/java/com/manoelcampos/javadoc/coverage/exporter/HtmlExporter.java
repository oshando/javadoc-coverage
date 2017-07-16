/*
 * Copyright 2017-2017 Manoel Campos da Silva Filho
 *
 * Licensed under the General Public License Version 3 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    https://www.gnu.org/licenses/gpl-3.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.manoelcampos.javadoc.coverage.exporter;

import com.manoelcampos.javadoc.coverage.CoverageDoclet;
import com.manoelcampos.javadoc.coverage.Utils;
import com.manoelcampos.javadoc.coverage.stats.*;
import com.sun.javadoc.PackageDoc;

import java.io.FileNotFoundException;
import java.util.List;

/**
 * Exports the JavaDoc coverage report to an HTML file.
 *
 * @author Manoel Campos da Silva Filho
 * @since 1.0.0
 */
public class HtmlExporter extends AbstractDataExporter {
    public static final String COLUMNS = "<td>%s</td><td>%s</td><td>%s</td><td>%s</td><td>%s</td><td>%s</td><td>%.2f%%</td>\n";

    public HtmlExporter(final CoverageDoclet doclet) throws FileNotFoundException {
        super(doclet, ".html");
    }

    @Override
    public boolean build(){
        try{
            start();

            exportClassesDocStats();
            exportPackagesDocStats();
            getWriter().printf("<tr>"+COLUMNS+"</tr>", "Project Documentation Coverage", "", "", "", "", "", getStats().getDocumentedMembersPercent());

            finish();

            System.out.printf("\nJavaDoc Coverage report saved to %s\n", getFile().getAbsolutePath());
            return true;
        } finally {
            getWriter().close();
        }
    }

    private void start() {
        getWriter().println("<!DOCTYPE html>\n<html lang=en>");
        getWriter().println("<head>");
        getWriter().println("    <title>JavaDoc Coverage Report</title>");
        getWriter().println("    <meta charset='utf-8'>");
        getWriter().println("    <meta content='width=device-width,initial-scale=1' name=viewport>");
        getWriter().println("    <link rel='stylesheet' href='https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css'>\n");
        getWriter().println("    <script src='https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/js/bootstrap.min.js'></script>\n");
        getWriter().println("</head>");
        getWriter().println("<body>");
        getWriter().println("<div class='table-responsive'> ");
        getWriter().println("<table class='table table-bordered'>");
        getWriter().println("<caption class='text-center'>JavaDoc Coverage Report</caption>");
        getWriter().println("<thead class='thead-inverse'>");
        getWriter().println("<tr>");
        getWriter().println("<th>Element Type</th><th>Name</th><th>Package</th><th>Documentable Members</th><th>Undocumented</th><th>Documented</th><th>Documented Percent</th>");
        getWriter().println("</tr>");
        getWriter().println("</thead>");
        getWriter().println("<tbody>");
    }

    private void finish() {
        getWriter().println("</tbody>");
        getWriter().println("</table>");
        getWriter().println("</div>");
        getWriter().println("</body>");
        getWriter().flush();
    }

    private void exportPackagesDocStats() {
        exportMembersDocStatsSummary(getStats().getPackagesDocStats());
        for (final PackageDoc doc : getStats().getPackagesDocStats().getPackagesDoc()) {
            getWriter().println("<tr>");
            final boolean documented = Utils.isNotStringEmpty(doc.getRawCommentText());
            final double coverage = Utils.boolToInt(documented)*100;
            getWriter().printf(COLUMNS, "Package", doc.name(), "", "", "", documented, coverage);
            getWriter().println("</tr>");
        }
    }

    private void exportClassesDocStats() {
        exportMembersDocStatsSummary(getStats().getClassesDocStats());
        for (final ClassDocStats classDocStats : getStats().getClassesDocStats().getClassesList()) {
            exportMembersDocStatsSummary(classDocStats, 2, classDocStats.getName(), classDocStats.getPackageName());
            exportMembersDocStatsSummary(classDocStats.getFieldsStats(), 3);
            exportMethodsDocStats(classDocStats.getConstructorsStats());
            exportMethodsDocStats(classDocStats.getMethodsStats());
        }
    }

    private void exportMethodsDocStats(final List<MethodDocStats> methods) {
        for (MethodDocStats m : methods) {
            exportMembersDocStatsSummary(m, 4, m.getMethodName(), "");
            exportMembersDocStatsSummary(m.getParamsStats(), 5);
            exportMembersDocStatsSummary(m.getThrownExceptions(), 5);
        }
    }

    private void exportMembersDocStatsSummary(final MembersDocStats membersDocStats, final int indentLevel) {
        exportMembersDocStatsSummary(membersDocStats, indentLevel, "","");
    }

    private void exportMembersDocStatsSummary(final MembersDocStats membersDocStats) {
        exportMembersDocStatsSummary(membersDocStats, 1, "","");
    }

    private void exportMembersDocStatsSummary(final MembersDocStats membersDocStats, final int indentLevel, final String name, final String pkg) {
        if(!membersDocStats.isPrintIfNoMembers() && membersDocStats.getMembersNumber() == 0){
            return;
        }

        getWriter().println("<tr>");
        final int len = indentLevel*4 - 3;
        final String type = String.format("%"+len+"s", "").replace(" ", "&nbsp;") + membersDocStats.getType();
        getWriter().printf(COLUMNS,
                type,
                name, pkg,
                membersDocStats.getMembersNumber(),
                membersDocStats.getUndocumentedMembers(),
                membersDocStats.getDocumentedMembers(),
                membersDocStats.getDocumentedMembersPercent());
        getWriter().println("</tr>");
    }

}