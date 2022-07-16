package antifraud.Service.Utils;

import java.util.Arrays;

public class Support {
    public boolean check_valid(String ip){
        String[] check_valid = ip.split("\\.");
        if(check_valid.length==4){
            return Arrays.stream(check_valid)
                    .map(Long::parseLong)
                    .noneMatch(x -> x > 255 || x < 0);
        }else{
            return false;
        }
    }
    public boolean check_card(String value){
        int sum = Character.getNumericValue(value.charAt(value.length() - 1));
        int parity = value.length() % 2;
        for (int i = value.length() - 2; i >= 0; i--) {
            int summand = Character.getNumericValue(value.charAt(i));
            if (i % 2 == parity) {
                int product = summand * 2;
                summand = (product > 9) ? (product - 9) : product;
            }
            sum += summand;
        }
        return (sum % 10) == 0;
    }
}
