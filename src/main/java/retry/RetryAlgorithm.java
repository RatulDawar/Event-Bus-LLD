package retry;

import exception.RetryLimitExceededException;
import exception.RetryAbleException;
import java.util.function.Function;

 public class RetryAlgorithm<PARAMETER,RESULT> {
    private final Integer MAX_ATTEMPTS;
    public RetryAlgorithm(Integer maxAttempts) {
        this.MAX_ATTEMPTS = maxAttempts;
    }

     public Integer getMAX_ATTEMPTS() {
         return MAX_ATTEMPTS;
     }

     public RESULT attempt(Function<PARAMETER,RESULT> task, PARAMETER parameter, int attempts, Function<Integer,Long> retryTimeCalculator ) throws InterruptedException {
        try{
            return task.apply(parameter);
        }catch(Exception e){
            if(e.getCause() instanceof RetryAbleException) {
                if (attempts == this.MAX_ATTEMPTS) {
                    throw new RetryLimitExceededException();
                }else{
                    return attempt(task,parameter,attempts+1,retryTimeCalculator);
                    try {
                        Thread.sleep(retryTimeCalculator.apply(attempts));
                    }catch (InterruptedException interruptedException){
                        throw new InterruptedException();
                    }

                }
            }
        }

    }

}

class PeriodicRetry<PARAMETER,RESULT> extends RetryAlgorithm<PARAMETER,RESULT> {
    private PeriodicRetry(@Named("periodic-retry-attempts" )final int maxAttempts, @Named("periodic-retry-wait") final long waitTimeInMillis){
        super(maxAttempts,(__)->waitTimeInMillis);
    }
}

class ExponentialBackoff<PARAMETER,RESULT> extends RetryAlgorithm<PARAMETER,RESULT>{

    public ExponentialBackoff(Integer maxAttempts) {
        super(maxAttempts, (attempts) -> (long) Math.pow(2,attempts - 1));
    }
}
