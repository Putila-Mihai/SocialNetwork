package domain;

import java.time.LocalDateTime;


public class Friendship extends Entity<Tuple<Long,Long>> {

    private final LocalDateTime date;


    public Friendship() {
        date = LocalDateTime.now();
    }

    /**
     *
     * @return the date when the friendship was created
     */
    public LocalDateTime getDate() {
        return date;
    }
}
