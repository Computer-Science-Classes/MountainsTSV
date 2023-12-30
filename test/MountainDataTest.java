import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.Test;

public class MountainDataTest {

    @Test
    public void testValidateRecord() {
        MountainDataTest processor = new MountainDataTest();
        assertDoesNotThrow(() -> processor.validateRecord("USA\\tMountain\\tEverest\\t28.0000\\t86.9250\\t8848 m"));
        assertThrows(CustomException.class,
                () -> processor.validateRecord("USA\\tMountain\\tEverest\\t28.0000\\t86.9250"));
        assertThrows(CustomException.class,
                () -> processor.validateRecord("USA\\tMountain\\tEverest\\t28.0000\\t86.9250\\t-8848 m"));
    }

    @Test
    public void testUpdateExtremeMountains() {
        MountainDataTest processor = new MountainDataTest();
        assertDoesNotThrow(
                () -> processor.updateExtremeMountains("USA\\tMountain\\tEverest\\t28.0000\\t86.9250\\t8848 m", 8848));
        assertEquals("Everest", processor.getTallestMountain());

    }

    @Test
    public void testGetMetadata() {
        MountainDataTest processor = new MountainDataTest();
        String metadata = processor.getMetadata();
        assertTrue(metadata.contains("Valid Records"));
        assertTrue(metadata.contains("Corrupted Records"));
        assertTrue(metadata.contains("Shortest Records"));
        assertTrue(metadata.contains("Tallest Mountain"));

    }

}
