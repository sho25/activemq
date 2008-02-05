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
operator|.
name|util
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
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|Service
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
comment|/**  * A helper class for working with services together with a useful base class  * for service implementations.  *   * @version $Revision: 1.1 $  */
end_comment

begin_class
specifier|public
specifier|abstract
class|class
name|ServiceSupport
implements|implements
name|Service
block|{
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
name|ServiceSupport
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
name|AtomicBoolean
name|started
init|=
operator|new
name|AtomicBoolean
argument_list|(
literal|false
argument_list|)
decl_stmt|;
specifier|private
name|AtomicBoolean
name|stopping
init|=
operator|new
name|AtomicBoolean
argument_list|(
literal|false
argument_list|)
decl_stmt|;
specifier|private
name|AtomicBoolean
name|stopped
init|=
operator|new
name|AtomicBoolean
argument_list|(
literal|false
argument_list|)
decl_stmt|;
specifier|public
specifier|static
name|void
name|dispose
parameter_list|(
name|Service
name|service
parameter_list|)
block|{
try|try
block|{
name|service
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Could not stop service: "
operator|+
name|service
operator|+
literal|". Reason: "
operator|+
name|e
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
specifier|public
name|void
name|start
parameter_list|()
throws|throws
name|Exception
block|{
if|if
condition|(
name|started
operator|.
name|compareAndSet
argument_list|(
literal|false
argument_list|,
literal|true
argument_list|)
condition|)
block|{
name|boolean
name|success
init|=
literal|false
decl_stmt|;
try|try
block|{
name|doStart
argument_list|()
expr_stmt|;
name|success
operator|=
literal|true
expr_stmt|;
block|}
finally|finally
block|{
name|started
operator|.
name|set
argument_list|(
name|success
argument_list|)
expr_stmt|;
block|}
block|}
block|}
specifier|public
name|void
name|stop
parameter_list|()
throws|throws
name|Exception
block|{
if|if
condition|(
name|stopped
operator|.
name|compareAndSet
argument_list|(
literal|false
argument_list|,
literal|true
argument_list|)
condition|)
block|{
name|stopping
operator|.
name|set
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|ServiceStopper
name|stopper
init|=
operator|new
name|ServiceStopper
argument_list|()
decl_stmt|;
try|try
block|{
name|doStop
argument_list|(
name|stopper
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|stopper
operator|.
name|onException
argument_list|(
name|this
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
name|stopped
operator|.
name|set
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|started
operator|.
name|set
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|stopping
operator|.
name|set
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|stopper
operator|.
name|throwFirstException
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**      * @return true if this service has been started      */
specifier|public
name|boolean
name|isStarted
parameter_list|()
block|{
return|return
name|started
operator|.
name|get
argument_list|()
return|;
block|}
comment|/**      * @return true if this service is in the process of closing      */
specifier|public
name|boolean
name|isStopping
parameter_list|()
block|{
return|return
name|stopping
operator|.
name|get
argument_list|()
return|;
block|}
comment|/**      * @return true if this service is closed      */
specifier|public
name|boolean
name|isStopped
parameter_list|()
block|{
return|return
name|stopped
operator|.
name|get
argument_list|()
return|;
block|}
specifier|protected
specifier|abstract
name|void
name|doStop
parameter_list|(
name|ServiceStopper
name|stopper
parameter_list|)
throws|throws
name|Exception
function_decl|;
specifier|protected
specifier|abstract
name|void
name|doStart
parameter_list|()
throws|throws
name|Exception
function_decl|;
block|}
end_class

end_unit

