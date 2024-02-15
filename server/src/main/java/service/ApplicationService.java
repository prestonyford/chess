package service;

public class ApplicationService extends Service {
    private static final ApplicationService INSTANCE = new ApplicationService();
    private ApplicationService() {}
    public static ApplicationService getInstance() {
        return INSTANCE;
    }
    public void bigRedButton() {
        db.clear();
    }
}
