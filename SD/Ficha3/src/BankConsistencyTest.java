import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class BankConsistencyTest {

    public static void main(String[] args) throws InterruptedException {
        Bank bank = new Bank();

        int X = 50; // número de contas
        int Y = 20; // número a fechar

        Random rand = new Random();

        // 1) Criar X contas
        int[] ids = new int[X];
        int totalInicialReal = 0;
        for (int i = 0; i < X; i++) {
            int saldo = rand.nextInt(200) + 100; // entre 100 e 299
            ids[i] = bank.createAccount(saldo);
            totalInicialReal += saldo;
        }
        System.out.println("Total inicial real: " + totalInicialReal);

        // 2) Conjunto thread-safe para registar contas fechadas
        Set<Integer> fechadas = Collections.newSetFromMap(new ConcurrentHashMap<>());
        AtomicInteger totalRemovido = new AtomicInteger(0);

        // 3) Workers concorrentes
        ExecutorService exec = Executors.newFixedThreadPool(8);

        Runnable worker = () -> {
            for (int i = 0; i < 2000; i++) {
                int op = rand.nextInt(3);
                int a = ids[rand.nextInt(X)];
                // se já está fechada, pular tentativas (opcional)
                if (fechadas.contains(a)) continue;
                switch (op) {
                    case 0 -> bank.deposit(a, 5);
                    case 1 -> bank.withdraw(a, 5);
                    case 2 -> {
                        int b = ids[rand.nextInt(X)];
                        if (fechadas.contains(b)) continue;
                        bank.transfer(a, b, 1);
                    }
                }
            }
        };

        // 4) Closer — fecha Y contas e regista quais foram fechadas
        Runnable closer = () -> {
            Random r = new Random();
            while (fechadas.size() < Y) {
                int id = ids[r.nextInt(X)];
                if (fechadas.contains(id)) continue;
                int saldo = bank.closeAccount(id);
                if (saldo > 0) {
                    totalRemovido.addAndGet(saldo);
                    fechadas.add(id);
                    System.out.println("Conta fechada: " + id + " saldo: " + saldo);
                }
            }
        };

        // lançar tarefas
        for (int i = 0; i < 5; i++) exec.submit(worker);
        exec.submit(closer);

        exec.shutdown();
        exec.awaitTermination(60, TimeUnit.SECONDS);

        // 5) Calcular total esperado e total do banco apenas com ids abertos
        int totalEsperado = totalInicialReal - totalRemovido.get();
        System.out.println("\nTotal removido (contas fechadas): " + totalRemovido.get());
        System.out.println("Total esperado final: " + totalEsperado);

        int[] idsAbertos = Arrays.stream(ids)
                .filter(id -> !fechadas.contains(id))
                .toArray();

        int totalBanco = bank.totalBalance(idsAbertos);
        System.out.println("Total calculado pelo banco (somente ids abertos): " + totalBanco);

        if (totalEsperado == totalBanco) {
            System.out.println("\n✔ CONSISTENTE! O dinheiro total bate certo.");
        } else {
            System.out.println("\n❌ INCONSISTENTE! Houve erro no sistema de concorrência.");
        }
    }
}
