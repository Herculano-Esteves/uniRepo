import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.*;

class Contact {
    private String name;
    private int age;
    private long phoneNumber;
    private String company;     // Pode ser null
    private ArrayList<String> emails;

    public Contact(String name, int age, long phoneNumber, String company, List<String> emails) {
        this.name = name;
        this.age = age;
        this.phoneNumber = phoneNumber;
        this.company = company;
        this.emails = new ArrayList<String>(emails);
    }

    public String name() { return name; }
    public int age() { return age; }
    public long phoneNumber() { return phoneNumber; }
    public String company() { return company; }
    public List<String> emails() { return new ArrayList<String>(emails); }

    // @TODO
    public void serialize(DataOutputStream out) throws IOException {
        out.writeUTF(name);
        out.writeInt(age);
        out.writeLong(phoneNumber);
        if(company == null){
            out.writeBoolean(false);
        } else {
            out.writeBoolean(true);
            out.writeUTF(company);
        }
        out.writeInt(emails.size());
        for(String email : emails){
            out.writeUTF(email);
        }
        out.flush();
    }

    // @TODO
    public static Contact deserialize(DataInputStream in) throws IOException {
        try{
            String name = in.readUTF();
            int age = in.readInt();
            long phonenumber = in.readLong();
            boolean bool = in.readBoolean();
            String company = null;
            if(bool){
                company = in.readUTF();
            }
            int num = in.readInt();
            List<String> emails = new ArrayList<String>();;
            while (num > 0){
                emails.add(in.readUTF());
                num--;
            }
            return new Contact(name, age, phonenumber, company, emails);
        } catch (IOException e){
            throw new RuntimeException(e);
        }
    }

    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append(this.name).append(";");
        builder.append(this.age).append(";");
        builder.append(this.phoneNumber).append(";");
        builder.append(this.company).append(";");
        builder.append(this.emails.toString());
        builder.append("}");
        return builder.toString();
    }

}
