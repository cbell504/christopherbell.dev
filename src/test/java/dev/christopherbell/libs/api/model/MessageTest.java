package dev.christopherbell.libs.api.model;

import dev.christopherbell.libs.common.api.common.TestUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class MessageTest {

  @Test
  public void testMessageBuilder() {
    var message = Message.builder()
        .code(TestUtil.TEST_CODE)
        .description(TestUtil.TEST_DESCRIPTION)
        .build();

    Assertions.assertNotNull(message);
    Assertions.assertEquals(TestUtil.TEST_CODE, message.getCode());
    Assertions.assertEquals(TestUtil.TEST_DESCRIPTION, message.getDescription());
  }

  @Test
  public void testMessageSetters() {
    var message = Message.builder()
        .build();
    message.setCode(TestUtil.TEST_CODE);
    message.setDescription(TestUtil.TEST_DESCRIPTION);

    Assertions.assertNotNull(message);
    Assertions.assertEquals(TestUtil.TEST_CODE, message.getCode());
    Assertions.assertEquals(TestUtil.TEST_DESCRIPTION, message.getDescription());
  }
}