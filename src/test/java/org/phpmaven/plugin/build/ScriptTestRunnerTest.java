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

package org.phpmaven.plugin.build;

import java.io.File;

import org.phpmaven.plugin.build.script.ScriptTestRunnerMojo;

import junit.framework.Assert;
import junit.framework.TestCase;

public class ScriptTestRunnerTest extends TestCase {
    public void testReplace() {
        ScriptTestRunnerMojo mojo= new ScriptTestRunnerMojo();
        String command = mojo.replaceCommandArgs("hansi *{testFolder}", "testFolder", "/home/cw");
        System.out.println(command);
        System.out.println(File.pathSeparator);
        Assert.assertEquals("hansi /home/cw", command);
    }
}