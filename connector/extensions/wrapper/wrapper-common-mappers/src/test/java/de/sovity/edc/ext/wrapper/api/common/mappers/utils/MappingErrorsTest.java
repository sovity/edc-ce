package de.sovity.edc.ext.wrapper.api.common.mappers.utils;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class MappingErrorsTest {


    @Test
    void testMappingErrors() {
        var parent = MappingErrors.root();
        parent.add("a");

        var childObj = parent.forChildObject("child");
        childObj.add("b");

        var childObjArray = childObj.forChildArrayElement(3);
        childObjArray.add("c");

        assertThat(parent.getErrors()).containsExactly(
                "$: a",
                "$.child: b",
                "$.child[3]: c"
        );
    }
}
