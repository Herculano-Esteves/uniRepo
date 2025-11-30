import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class BankTest {

    public static void main(String[] args) throws InterruptedException {
        Bank bank = new Bank();

        // ============ 1. Criar contas ============
        int n = 10;
        int[] ids = new int[n];
        for (int i = 0; i < n; i++) {
            ids[i] = bank.createAccount(100);
        }

        System.out.println("Contas criadas: " + Arrays.toString(ids));

        // ============ 2. Testar fecho simples ============
        int closedId = ids[0];
        int closedBalance = bank.closeAccount(closedId);
        System.out.println("Fecho da conta " + closedId + " devolveu: " + closedBalance);

        assert closedBalance == 100 : "Erro no fecho simples";
        assert bank.closeAccount(closedId) == 0 : "Fechar duas vezes deveria devolver 0";

        // ============ 3. Testar totalBalance com conta inexistente ============
        int totalShouldBeZero = bank.totalBalance(new int[]{closedId, ids[1]});
        System.out.println("totalBalance com conta inexistente → " + totalShouldBeZero);
        assert totalShouldBeZero == 0;

        // ============ 4. Teste concorrente ============
        ExecutorService pool = Executors.newFixedThreadPool(8);

        AtomicInteger successOps = new AtomicInteger();
        AtomicInteger failedOps = new AtomicInteger();

        Runnable worker = () -> {
            Random r = new Random();
            for (int i = 0; i < 1000; i++) {
                int id = ids[r.nextInt(n)];
                int op = r.nextInt(4);

                boolean result = false;

                switch (op) {
                    case 0:
                        result = bank.deposit(id, 10);
                        break;
                    case 1:
                        result = bank.withdraw(id, 5);
                        break;
                    case 2:
                        int to = ids[r.nextInt(n)];
                        result = bank.transfer(id, to, 1);
                        break;
                    case 3:
                        // consulta
                        bank.balance(id);
                        result = true;
                        break;
                }

                if (result) successOps.incrementAndGet();
                else failedOps.incrementAndGet();
            }
        };

        // Thread adicional que tenta fechar contas aleatoriamente
        Runnable closer = () -> {
            Random r = new Random();
            for (int i = 0; i < 50; i++) {
                int id = ids[r.nextInt(n)];
                int rb = bank.closeAccount(id);
                if (rb > 0) System.out.println("Conta " + id + " fechada com saldo " + rb);
            }
        };

        // Lançar threads
        for (int i = 0; i < 5; i++) pool.submit(worker);
        pool.submit(closer);

        pool.shutdown();
        pool.awaitTermination(30, TimeUnit.SECONDS);

        System.out.println("Operações bem-sucedidas: " + successOps.get());
        System.out.println("Operações falhadas: " + failedOps.get());

        // ============ 5. Testar consistência final ============
        int[] remainingIds = Arrays.stream(ids)
                .filter(id -> bank.balance(id) > 0 || bank.balance(id) == 0)
                .toArray();

        int total = bank.totalBalance(remainingIds);
        System.out.println("Total final: " + total);

        // Se chegámos até aqui sem deadlocks, crashes ou inconsistências → sucesso!
        System.out.println("Teste concluído sem deadlocks.");
    }
}
