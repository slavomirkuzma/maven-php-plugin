/**
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.phpmaven.plugin.php;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;

/**
 * Helper to sum up the phpunit testcases.
 * 
 * @author Martin Eisengardt
 */
public class PhpUnitTestfileWalker extends AbstractPhpWalkHelper {

    /**
     * Text to skip all testing.
     */
    public static final String SKIP_TESTS_TEXT = "Configured to skip tests.";
    
    /**
     * Failure text on no tests.
     */
    public static final String FAIL_ON_NO_TEST_TEXT = "No test case found; failing because failIfNoTests set to true.";
    
    /**
     * The mojo configuration.
     */
    private IPhpunitConfigurationMojo config;
    
    /**
     * The php files to be tested.
     */
    private List<File> testFiles = new ArrayList<File>();

    /**
     * The constructor.
     * 
     * @param config Mojo configuration
     * @throws MultiException thrown on php failures
     * @throws MojoFailureException thrown if there are no tests and we are configured to fail
     */
    public PhpUnitTestfileWalker(IPhpunitConfigurationMojo config) throws MultiException, MojoFailureException {
        super(config);
        this.config = config;
        
        if (config.isSkipTests()) {
            config.getLog().info(SKIP_TESTS_TEXT);
            return;
        }
        
        config.getLog().debug("Starting test file walker.");
        for (final String testCompiledSource : config.getProject().getTestCompileSourceRoots())
        {
            if (config.getLog().isDebugEnabled()) {
                config.getLog().debug("param testCompilSourceRoot: " + testCompiledSource);
                config.getLog().debug("param isFailIfNoTests: " + (config.isFailIfNoTests() ? "true" : "false"));
                config.getLog().debug("param testPostfix: " + config.getTestPostfix());
                config.getLog().debug("param phpFileEnding: " + config.getPhpFileEnding());
                if (config.getExcludes() != null) {
                    for (final String exclude : config.getExcludes()) {
                        config.getLog().debug("param excludes: " + exclude);
                    }
                }
                if (config.getIncludes() != null) {
                    for (final String include : config.getIncludes()) {
                        config.getLog().debug("param includes: " + include);
                    }
                }
            }
            final File testSourceFolder = new File(testCompiledSource);
            if (!testSourceFolder.isDirectory()) {
                config.getLog().info("No test cases found; skipping.");
                if (config.isFailIfNoTests()) {
                    config.getLog().info(FAIL_ON_NO_TEST_TEXT);
                    throw new MojoFailureException(FAIL_ON_NO_TEST_TEXT);
                }
                return;
            }
            
            this.goRecursiveAndCall(testSourceFolder);
        }
        
        if (config.isFailIfNoTests() && this.testFiles.isEmpty()) {
            config.getLog().info(FAIL_ON_NO_TEST_TEXT);
            throw new MojoFailureException(FAIL_ON_NO_TEST_TEXT);
        }
    }

    /**
     * Tests if the given file is a test file.
     * 
     * @param file file to be tested
     * @return true if it is a test file.
     */
    protected boolean isTestFile(File file) {
        config.getLog().debug("Testing file " + file.getAbsolutePath() + " for test file.");
        // only files ending with "Test" are treated as testcase files
        if (!file.getName().toLowerCase().endsWith(
            config.getTestPostfix().toLowerCase() + "." + config.getPhpFileEnding())) {
            config.getLog().debug("Ignoring file. no test file.");
            return false;
        }

        return true;
    }

    @Override
    protected void handlePhpFile(File file) throws MojoExecutionException {
        if (!isTestFile(file)) {
            config.getLog().debug("This is no test file: skipping " + file.getAbsolutePath());
            return;
        }
        
        config.getLog().debug("Adding file as test file " + file.getAbsolutePath());
        this.testFiles.add(file);
    }

    @Override
    protected void handleProcessedFile(File file) throws MojoExecutionException {
        // does nothing
    }
    
    /**
     * The test files to be tested.
     * @return test files
     */
    public Iterable<File> getTestFiles() {
        config.getLog().debug("Returning test files " + this.testFiles);
        return this.testFiles;
    }

}
