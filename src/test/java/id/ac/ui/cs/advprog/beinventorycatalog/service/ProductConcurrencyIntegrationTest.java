package id.ac.ui.cs.advprog.beinventorycatalog.service;

import id.ac.ui.cs.advprog.beinventorycatalog.model.Product;
import id.ac.ui.cs.advprog.beinventorycatalog.repository.ProductRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class ProductConcurrencyIntegrationTest {

    @Autowired
    private ProductService productService;

    @Autowired
    private ProductRepository productRepository;

    private Product testProduct;

    @BeforeEach
    void setUp() {
        Product product = Product.builder()
                .name("Test Flash Sale Item")
                .description("Limited edition item")
                .price(100.0)
                .stock(10) // Hanya 10 stok
                .originCountry("ID")
                .jastiperId("test-jastiper")
                .build();
        testProduct = productRepository.save(product);
    }

    @AfterEach
    void tearDown() {
        productRepository.deleteAll();
    }

    @Test
    void testConcurrentReserveStock_NoOverselling() throws InterruptedException {
        int numberOfThreads = 50; // 50 user mencoba memperebutkan 10 barang
        ExecutorService executorService = Executors.newFixedThreadPool(numberOfThreads);
        CountDownLatch latch = new CountDownLatch(numberOfThreads);
        
        AtomicInteger successfulReservations = new AtomicInteger(0);
        AtomicInteger failedReservations = new AtomicInteger(0);

        for (int i = 0; i < numberOfThreads; i++) {
            executorService.submit(() -> {
                try {
                    boolean success = productService.reserveStock(testProduct.getId(), 1);
                    if (success) {
                        successfulReservations.incrementAndGet();
                    } else {
                        failedReservations.incrementAndGet();
                    }
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
        executorService.shutdown();

        Product updatedProduct = productRepository.findById(testProduct.getId()).orElseThrow();

        // Verifikasi: Maksimal hanya 10 yang bisa sukses karena stock awal 10
        assertEquals(10, successfulReservations.get(), "Hanya 10 reservasi yang boleh sukses");
        assertEquals(40, failedReservations.get(), "40 reservasi harusnya gagal");
        assertEquals(0, updatedProduct.getStock(), "Stock di database harus tepat 0, tidak boleh negatif");
    }
}
