package edu.school21.processor;

import com.google.auto.service.AutoService;
import edu.school21.annotations.HtmlForm;
import edu.school21.annotations.HtmlInput;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@SupportedAnnotationTypes(value = {"edu.school21.annotations.HtmlForm", "edu.school21.annotations.HtmlInput"})
@SupportedSourceVersion(SourceVersion.RELEASE_8)
@AutoService(Processor.class)
public class HtmlProcessor extends AbstractProcessor {
    private final String HTML_FORM = "<form action = \"%s\" method = \"%s\">\n";
    private final String HTML_INPUT = "\t<input type = \"%s\" name = \"%s\" placeholder = \"%s\">\n";

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        Set<? extends Element> annotatedElements = roundEnv.getElementsAnnotatedWith(HtmlForm.class);
        for (Element e : annotatedElements) {
            List<? extends Element> listElement = e.getEnclosedElements();
            List<Annotation> listField = new ArrayList<>(listElement.size());
            for(Element element : listElement) {
                if(element.getAnnotation(HtmlInput.class) != null) {
                    Annotation annotationField = element.getAnnotation(HtmlInput.class);
                    listField.add(annotationField);
                }
            }
            createHtml(e.getAnnotation(HtmlForm.class), listField);
        }
        return true;
    }

    private void createHtml(HtmlForm form, List<Annotation> listField) {
        try (FileWriter fileHtml = new FileWriter("target/classes/" + form.fileName())) {
            fileHtml.write(String.format(HTML_FORM, form.action(), form.method()));
            for(Annotation f : listField) {
                HtmlInput input = (HtmlInput)f;
                fileHtml.write(String.format(HTML_INPUT, input.type(), input.name(), input.placeholder()));
            }
            fileHtml.write("\t<input type = \"submit\" value = \"Send\">\n");
            fileHtml.write("</form>");
            fileHtml.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
