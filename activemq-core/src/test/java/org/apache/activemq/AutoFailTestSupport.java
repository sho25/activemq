begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *      http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
package|package
name|org
operator|.
name|apache
operator|.
name|activemq
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|atomic
operator|.
name|AtomicBoolean
import|;
end_import

begin_import
import|import
name|junit
operator|.
name|framework
operator|.
name|TestCase
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|logging
operator|.
name|Log
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|logging
operator|.
name|LogFactory
import|;
end_import

begin_comment
comment|/**  * Enforces a test case to run for only an allotted time to prevent them from  * hanging and breaking the whole testing.  *   * @version $Revision: 1.0 $  */
end_comment

begin_class
specifier|public
specifier|abstract
class|class
name|AutoFailTestSupport
extends|extends
name|TestCase
block|{
specifier|public
specifier|static
specifier|final
name|int
name|EXIT_SUCCESS
init|=
literal|0
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|int
name|EXIT_ERROR
init|=
literal|1
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|Log
name|LOG
init|=
name|LogFactory
operator|.
name|getLog
argument_list|(
name|AutoFailTestSupport
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
name|long
name|maxTestTime
init|=
literal|5
operator|*
literal|60
operator|*
literal|1000
decl_stmt|;
comment|// 5 mins by default
specifier|private
name|Thread
name|autoFailThread
decl_stmt|;
specifier|private
name|boolean
name|verbose
init|=
literal|true
decl_stmt|;
specifier|private
name|boolean
name|useAutoFail
decl_stmt|;
comment|// Disable auto fail by default
specifier|private
name|AtomicBoolean
name|isTestSuccess
decl_stmt|;
specifier|protected
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
comment|// Runs the auto fail thread before performing any setup
if|if
condition|(
name|isAutoFail
argument_list|()
condition|)
block|{
name|startAutoFailThread
argument_list|()
expr_stmt|;
block|}
name|super
operator|.
name|setUp
argument_list|()
expr_stmt|;
block|}
specifier|protected
name|void
name|tearDown
parameter_list|()
throws|throws
name|Exception
block|{
name|super
operator|.
name|tearDown
argument_list|()
expr_stmt|;
comment|// Stops the auto fail thread only after performing any clean up
name|stopAutoFailThread
argument_list|()
expr_stmt|;
block|}
comment|/**      * Manually start the auto fail thread. To start it automatically, just set      * the auto fail to true before calling any setup methods. As a rule, this      * method is used only when you are not sure, if the setUp and tearDown      * method is propagated correctly.      */
specifier|public
name|void
name|startAutoFailThread
parameter_list|()
block|{
name|setAutoFail
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|isTestSuccess
operator|=
operator|new
name|AtomicBoolean
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|autoFailThread
operator|=
operator|new
name|Thread
argument_list|(
operator|new
name|Runnable
argument_list|()
block|{
specifier|public
name|void
name|run
parameter_list|()
block|{
try|try
block|{
comment|// Wait for test to finish succesfully
name|Thread
operator|.
name|sleep
argument_list|(
name|getMaxTestTime
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{
comment|// This usually means the test was successful
block|}
finally|finally
block|{
comment|// Check if the test was able to tear down succesfully,
comment|// which usually means, it has finished its run.
if|if
condition|(
operator|!
name|isTestSuccess
operator|.
name|get
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Test case has exceeded the maximum allotted time to run of: "
operator|+
name|getMaxTestTime
argument_list|()
operator|+
literal|" ms."
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|fatal
argument_list|(
literal|"Test case has exceeded the maximum allotted time to run of: "
operator|+
name|getMaxTestTime
argument_list|()
operator|+
literal|" ms."
argument_list|)
expr_stmt|;
name|System
operator|.
name|exit
argument_list|(
name|EXIT_ERROR
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
argument_list|,
literal|"AutoFailThread"
argument_list|)
expr_stmt|;
if|if
condition|(
name|verbose
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Starting auto fail thread..."
argument_list|)
expr_stmt|;
block|}
name|LOG
operator|.
name|info
argument_list|(
literal|"Starting auto fail thread..."
argument_list|)
expr_stmt|;
name|autoFailThread
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
comment|/**      * Manually stops the auto fail thread. As a rule, this method is used only      * when you are not sure, if the setUp and tearDown method is propagated      * correctly.      */
specifier|public
name|void
name|stopAutoFailThread
parameter_list|()
block|{
if|if
condition|(
name|isAutoFail
argument_list|()
operator|&&
name|autoFailThread
operator|!=
literal|null
operator|&&
name|autoFailThread
operator|.
name|isAlive
argument_list|()
condition|)
block|{
name|isTestSuccess
operator|.
name|set
argument_list|(
literal|true
argument_list|)
expr_stmt|;
if|if
condition|(
name|verbose
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Stopping auto fail thread..."
argument_list|)
expr_stmt|;
block|}
name|LOG
operator|.
name|info
argument_list|(
literal|"Stopping auto fail thread..."
argument_list|)
expr_stmt|;
name|autoFailThread
operator|.
name|interrupt
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**      * Sets the auto fail value. As a rule, this should be used only before any      * setup methods is called to automatically enable the auto fail thread in      * the setup method of the test case.      *       * @param val      */
specifier|public
name|void
name|setAutoFail
parameter_list|(
name|boolean
name|val
parameter_list|)
block|{
name|this
operator|.
name|useAutoFail
operator|=
name|val
expr_stmt|;
block|}
specifier|public
name|boolean
name|isAutoFail
parameter_list|()
block|{
return|return
name|this
operator|.
name|useAutoFail
return|;
block|}
comment|/**      * The assigned value will only be reflected when the auto fail thread has      * started its run. Value is in milliseconds.      *       * @param val      */
specifier|public
name|void
name|setMaxTestTime
parameter_list|(
name|long
name|val
parameter_list|)
block|{
name|this
operator|.
name|maxTestTime
operator|=
name|val
expr_stmt|;
block|}
specifier|public
name|long
name|getMaxTestTime
parameter_list|()
block|{
return|return
name|this
operator|.
name|maxTestTime
return|;
block|}
block|}
end_class

end_unit

