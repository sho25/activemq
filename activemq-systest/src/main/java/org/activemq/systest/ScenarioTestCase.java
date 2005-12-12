begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  *   * Copyright 2005 LogicBlaze, Inc. http://www.logicblaze.com  *   * Licensed under the Apache License, Version 2.0 (the "License");   * you may not use this file except in compliance with the License.   * You may obtain a copy of the License at   *   * http://www.apache.org/licenses/LICENSE-2.0  *   * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,   * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.   * See the License for the specific language governing permissions and   * limitations under the License.   *   **/
end_comment

begin_package
package|package
name|org
operator|.
name|activemq
operator|.
name|systest
package|;
end_package

begin_import
import|import
name|junit
operator|.
name|framework
operator|.
name|AssertionFailedError
import|;
end_import

begin_import
import|import
name|junit
operator|.
name|framework
operator|.
name|Test
import|;
end_import

begin_import
import|import
name|junit
operator|.
name|framework
operator|.
name|TestResult
import|;
end_import

begin_comment
comment|/**  * A JUnit {@link Test} for running a Scenario in a JUnit test suite.  *   * @version $Revision: 1.1 $  */
end_comment

begin_class
specifier|public
class|class
name|ScenarioTestCase
implements|implements
name|Test
block|{
specifier|private
name|Scenario
name|scenario
decl_stmt|;
specifier|private
name|String
name|description
decl_stmt|;
specifier|public
name|ScenarioTestCase
parameter_list|(
name|Scenario
name|scenario
parameter_list|,
name|String
name|description
parameter_list|)
block|{
name|this
operator|.
name|scenario
operator|=
name|scenario
expr_stmt|;
name|this
operator|.
name|description
operator|=
name|description
expr_stmt|;
block|}
specifier|public
name|int
name|countTestCases
parameter_list|()
block|{
return|return
literal|1
return|;
block|}
specifier|public
name|void
name|run
parameter_list|(
name|TestResult
name|result
parameter_list|)
block|{
name|result
operator|.
name|startTest
argument_list|(
name|this
argument_list|)
expr_stmt|;
try|try
block|{
name|scenario
operator|.
name|start
argument_list|()
expr_stmt|;
name|scenario
operator|.
name|run
argument_list|()
expr_stmt|;
name|scenario
operator|.
name|stop
argument_list|()
expr_stmt|;
name|result
operator|.
name|endTest
argument_list|(
name|this
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|AssertionFailedError
name|e
parameter_list|)
block|{
name|result
operator|.
name|addFailure
argument_list|(
name|this
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|e
parameter_list|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Failed to run test: "
operator|+
name|e
argument_list|)
expr_stmt|;
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
name|result
operator|.
name|addError
argument_list|(
name|this
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
try|try
block|{
name|scenario
operator|.
name|stop
argument_list|()
expr_stmt|;
name|scenario
operator|=
literal|null
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Failed to close down test: "
operator|+
name|e
argument_list|)
expr_stmt|;
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
block|}
block|}
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
name|description
return|;
block|}
block|}
end_class

end_unit

