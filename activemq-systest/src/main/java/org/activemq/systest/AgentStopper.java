begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  *<a href="http://activemq.org">ActiveMQ: The Open Source Message Fabric</a>  *  * Copyright 2005 (C) LogicBlaze, Inc. http://www.logicblaze.com  *  * Licensed under the Apache License, Version 2.0 (the "License");  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  * http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  *  **/
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

begin_comment
comment|/**  * A helper class used to stop a bunch of services, catching and logging any  * exceptions and then throwing the first exception when everything is stoped.  *   * @version $Revision: 1.1 $  */
end_comment

begin_class
specifier|public
class|class
name|AgentStopper
block|{
specifier|private
name|Exception
name|firstException
decl_stmt|;
comment|/**      * Stops the given service, catching any exceptions that are thrown.      */
specifier|public
name|void
name|stop
parameter_list|(
name|Agent
name|service
parameter_list|)
block|{
if|if
condition|(
name|service
operator|!=
literal|null
condition|)
block|{
try|try
block|{
name|service
operator|.
name|stop
argument_list|(
name|this
argument_list|)
expr_stmt|;
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
block|}
specifier|public
name|void
name|onException
parameter_list|(
name|Object
name|owner
parameter_list|,
name|Exception
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
throw|throw
name|firstException
throw|;
block|}
block|}
specifier|protected
name|void
name|logError
parameter_list|(
name|Object
name|service
parameter_list|,
name|Exception
name|e
parameter_list|)
block|{
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"Could not stop service: "
operator|+
name|service
operator|+
literal|". Reason: "
operator|+
name|e
argument_list|)
expr_stmt|;
name|e
operator|.
name|printStackTrace
argument_list|(
name|System
operator|.
name|err
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

