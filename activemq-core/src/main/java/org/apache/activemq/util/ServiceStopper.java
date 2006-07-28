begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  *  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  * http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Iterator
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
import|;
end_import

begin_comment
comment|/**  * A helper class used to stop a bunch of services, catching and logging any  * exceptions and then throwing the first exception when everything is stoped.  *   * @version $Revision: 1.1 $  */
end_comment

begin_class
specifier|public
class|class
name|ServiceStopper
block|{
specifier|private
name|Throwable
name|firstException
decl_stmt|;
comment|/**      * Stops the given service, catching any exceptions that are thrown.      */
specifier|public
name|void
name|stop
parameter_list|(
name|Service
name|service
parameter_list|)
block|{
try|try
block|{
if|if
condition|(
name|service
operator|!=
literal|null
condition|)
block|{
name|service
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|onException
argument_list|(
name|service
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**      * Performs the given code to stop some service handling the exceptions      * which may be thrown properly      */
specifier|public
name|void
name|run
parameter_list|(
name|Callback
name|stopClosure
parameter_list|)
block|{
try|try
block|{
name|stopClosure
operator|.
name|execute
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|e
parameter_list|)
block|{
name|onException
argument_list|(
name|stopClosure
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**      * Stops a list of services      */
specifier|public
name|void
name|stopServices
parameter_list|(
name|List
name|services
parameter_list|)
block|{
for|for
control|(
name|Iterator
name|iter
init|=
name|services
operator|.
name|iterator
argument_list|()
init|;
name|iter
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|Service
name|service
init|=
operator|(
name|Service
operator|)
name|iter
operator|.
name|next
argument_list|()
decl_stmt|;
name|stop
argument_list|(
name|service
argument_list|)
expr_stmt|;
block|}
block|}
specifier|public
name|void
name|onException
parameter_list|(
name|Object
name|owner
parameter_list|,
name|Throwable
name|e
parameter_list|)
block|{
name|logError
argument_list|(
name|owner
argument_list|,
name|e
argument_list|)
expr_stmt|;
if|if
condition|(
name|firstException
operator|==
literal|null
condition|)
block|{
name|firstException
operator|=
name|e
expr_stmt|;
block|}
block|}
comment|/**      * Throws the first exception that was thrown if there was one.      */
specifier|public
name|void
name|throwFirstException
parameter_list|()
throws|throws
name|Exception
block|{
if|if
condition|(
name|firstException
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|firstException
operator|instanceof
name|Exception
condition|)
block|{
name|Exception
name|e
init|=
operator|(
name|Exception
operator|)
name|firstException
decl_stmt|;
throw|throw
name|e
throw|;
block|}
elseif|else
if|if
condition|(
name|firstException
operator|instanceof
name|RuntimeException
condition|)
block|{
name|RuntimeException
name|e
init|=
operator|(
name|RuntimeException
operator|)
name|firstException
decl_stmt|;
throw|throw
name|e
throw|;
block|}
else|else
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Unknown type of exception: "
operator|+
name|firstException
argument_list|,
name|firstException
argument_list|)
throw|;
block|}
block|}
block|}
specifier|protected
name|void
name|logError
parameter_list|(
name|Object
name|service
parameter_list|,
name|Throwable
name|e
parameter_list|)
block|{
name|Log
name|log
init|=
name|LogFactory
operator|.
name|getLog
argument_list|(
name|service
operator|.
name|getClass
argument_list|()
argument_list|)
decl_stmt|;
name|log
operator|.
name|error
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
end_class

end_unit

