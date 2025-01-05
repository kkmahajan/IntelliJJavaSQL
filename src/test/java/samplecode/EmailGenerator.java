//package samplecode;
//
//import org.stringtemplate.v4.ST;
//
//public class EmailGenerator {
//    public static void main(String[] args) {
//        String template = "Hello $name$,\n\n" +
//                "Thank you for registering on $platform$. We're excited to have you!\n\n" +
//                "Best regards,\nThe $platform$ Team";
//
//        ST emailTemplate = new ST(template);
//        emailTemplate.add("name", "John Doe");
//        emailTemplate.add("platform", "TechWorld");
//
//        System.out.println(emailTemplate.render());
//    }
//}
