package tk.geniusman.downloader;

public enum Type {

    FORK_JOIN(ForkJoinDownloader.class), DEFAULT(DefaultDownloader.class), NEGATIVE(NegativeSizeDownloader.class);

    private Class<? extends Downloader> clazz;

    private Type(Class<? extends Downloader> clazz) {
        this.setClazz(clazz);
    }

    public Class<? extends Downloader> getClazz() {
        return clazz;
    }

    public void setClazz(Class<? extends Downloader> clazz) {
        this.clazz = clazz;
    }

}
