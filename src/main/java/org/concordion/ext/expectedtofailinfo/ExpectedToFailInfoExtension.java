package org.concordion.ext.expectedtofailinfo;

import org.concordion.api.Element;
import org.concordion.api.extension.ConcordionExtender;
import org.concordion.api.extension.ConcordionExtension;
import org.concordion.api.listener.ExampleEvent;
import org.concordion.api.listener.ExampleListener;

/**
 * Displays the Note and a Reason in the corresponding specification/markdown,
 * when the annotation expectedToFail is used.
 *
 * <p></p>
 * Sample usage:
 * <p></p>
 * <pre>
 * In a Fixture class annotate the class with:
 * \@Extensions({ ExpectedToFailInfoExtension.class })
 *
 * To a specification add:
 * ## [My Specification Name](- "Reason my specification is failing c:status=expectedToFail")
 * </pre>
 * <p></p>
 * In the completed specification:
 * <ul>
 * <li>Note: resolves to 'This example has been marked as EXPECTED_TO_FAIL'</li>
 * <li>Reason: Based on the sample usage above, would resolve to 'Reason my specification is failing'</li>
 * </ul>
 * @author Luke Pearson
 *
 */
public class ExpectedToFailInfoExtension implements ConcordionExtension, ExampleListener {

    private String STYLE = "font-weight: normal; text-decoration: none; color: #bb5050;";
    private String EXPECTED_TO_FAIL_MESSAGE_SIZE = "h3";

    private final String ORIGINAL_TEXT = "This example has been marked as EXPECTED_TO_FAIL";

    private String NOTE = "Note";
    private String REASON = "Reason";


    @Override
    public void addTo(ConcordionExtender concordionExtender) {
        concordionExtender.withExampleListener(this);
    }

    @Override
    public void beforeExample(ExampleEvent event) {}

    @Override
    public void afterExample(ExampleEvent event) {
        String exampleName = event.getExampleName();
        Element body = event.getElement().getRootElement().getFirstChildElement("body");

        if (body != null) {
            Element[] divs = body.getChildElements("div");

            for (Element div : divs) {
                String concordionStatusAttribute = div.getAttributeValue("status", "http://www.concordion.org/2007/concordion");
                String concordionExampleAttribute = div.getAttributeValue("example", "http://www.concordion.org/2007/concordion");

                if (concordionStatusAttribute != null && concordionExampleAttribute != null &&
                        concordionStatusAttribute.equalsIgnoreCase("expectedToFail") && concordionExampleAttribute.equals(exampleName)) {

                    Element failingDiv = div.getFirstChildElement("p");

                    failingDiv.appendSister(createANewMessage(REASON + ": " + exampleName, REASON));
                    failingDiv.appendSister(createANewMessage(NOTE + ": " + ORIGINAL_TEXT, NOTE));

                    div.removeChild(div.getFirstChildElement("p"));
                }
            }
        }
    }

    public ExpectedToFailInfoExtension setStyle(String style) {
        this.STYLE = style;
        return this;
    }

    /**
     * Takes a string value to set as the html tag for the message (refer to default for baseline).
     * @param headerElementSize
     * @return
     */
    public ExpectedToFailInfoExtension setHeaderElementSize(String headerElementSize) {
        this.EXPECTED_TO_FAIL_MESSAGE_SIZE = headerElementSize;
        return this;
    }

    public ExpectedToFailInfoExtension setNoteMessage(String newNoteMessage) {
        this.NOTE = newNoteMessage;
        return this;
    }

    public ExpectedToFailInfoExtension setReasonMessage(String newReasonMessage) {
        this.REASON = newReasonMessage;
        return this;
    }

    private Element createANewMessage(String message, String className) {
        Element originalExpectedToFailNote = new Element(EXPECTED_TO_FAIL_MESSAGE_SIZE);

        originalExpectedToFailNote.appendText(message);
        originalExpectedToFailNote.addStyleClass(className);
        originalExpectedToFailNote.addAttribute("style", STYLE);

        return originalExpectedToFailNote;
    }

}
