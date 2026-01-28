package lomtev.dev.properties;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class AccountProperties {
    private final BigDecimal defaultAmount;
    private final int transferCommission;

    public AccountProperties(
            @Value("${account.default-amount}") BigDecimal defaultAmount,
            @Value("${account.transfer-commission}") int transferCommission
    ) {
        this.defaultAmount = defaultAmount;
        this.transferCommission = transferCommission;
    }

    public BigDecimal getDefaultAmount() {
        return defaultAmount;
    }

    public int getTransferCommission() {
        return transferCommission;
    }
}
