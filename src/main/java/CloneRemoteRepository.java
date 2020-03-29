

import org.apache.commons.io.FileUtils;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.ProgressMonitor;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;

import java.io.File;
import java.io.IOException;

public class CloneRemoteRepository {

    private static final String REMOTE_URL = "https://github.com/movilidadagil/CucumberWork.git";

    public static void main(String[] args) throws IOException, GitAPIException {
        // prepare a new folder for the cloned repository
        File localPath = new File("C:\\Users\\xyx\\Downloads\\githubautomationtest");

        final String user = "xxx";
        final String pass = "xxxx";
        if (user != null && pass != null) {

        }
        // then clone
        System.out.println("Cloning from " + REMOTE_URL + " to " + localPath);
      //  cloneCommand.setCredentialsProvider( new UsernamePasswordCredentialsProvider( "user", "password" ) );

        try (Git result = Git.cloneRepository()
                .setURI(REMOTE_URL)
                .setDirectory(localPath)
                .setCredentialsProvider(new UsernamePasswordCredentialsProvider(user,pass))
                .setProgressMonitor(new SimpleProgressMonitor())
                .call()) {
	        // Note: the call() returns an opened repository already which needs to be closed to avoid file handle leaks!
	        System.out.println("Having repository: " + result.getRepository().getDirectory());
        }

        // clean up here to not keep using more and more disk-space for these samples
    //    FileUtils.deleteDirectory(localPath);
    }

    private static class SimpleProgressMonitor implements ProgressMonitor {
        @Override
        public void start(int totalTasks) {
            System.out.println("Starting work on " + totalTasks + " tasks");
        }

        @Override
        public void beginTask(String title, int totalWork) {
            System.out.println("Start " + title + ": " + totalWork);
        }

        @Override
        public void update(int completed) {
            System.out.print(completed + "-");
        }

        @Override
        public void endTask() {
            System.out.println("Done");
        }

        @Override
        public boolean isCancelled() {
            return false;
        }
    }
}