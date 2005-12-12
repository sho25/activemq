begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**   *   * Copyright 2005 LogicBlaze, Inc. (http://www.logicblaze.com)  *   * Licensed under the Apache License, Version 2.0 (the "License");   * you may not use this file except in compliance with the License.   * You may obtain a copy of the License at   *   * http://www.apache.org/licenses/LICENSE-2.0  *   * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,   * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.   * See the License for the specific language governing permissions and   * limitations under the License.   *   **/
end_comment

begin_package
package|package
name|org
operator|.
name|activecluster
operator|.
name|impl
package|;
end_package

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
name|org
operator|.
name|activecluster
operator|.
name|Node
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|Message
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|MessageListener
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|ObjectMessage
import|;
end_import

begin_comment
comment|/**  * A JMS MessageListener which processes inbound messages and  * applies them to a StateService  *  * @version $Revision: 1.2 $  */
end_comment

begin_class
specifier|public
class|class
name|StateConsumer
implements|implements
name|MessageListener
block|{
specifier|private
specifier|final
specifier|static
name|Log
name|log
init|=
name|LogFactory
operator|.
name|getLog
argument_list|(
name|StateConsumer
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
name|StateService
name|stateService
decl_stmt|;
specifier|public
name|StateConsumer
parameter_list|(
name|StateService
name|stateService
parameter_list|)
block|{
if|if
condition|(
name|stateService
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Must specify a valid StateService implementation"
argument_list|)
throw|;
block|}
name|this
operator|.
name|stateService
operator|=
name|stateService
expr_stmt|;
block|}
specifier|public
name|void
name|onMessage
parameter_list|(
name|Message
name|message
parameter_list|)
block|{
if|if
condition|(
name|log
operator|.
name|isDebugEnabled
argument_list|()
condition|)
block|{
name|log
operator|.
name|debug
argument_list|(
literal|"Received cluster data message!: "
operator|+
name|message
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|message
operator|instanceof
name|ObjectMessage
condition|)
block|{
name|ObjectMessage
name|objectMessage
init|=
operator|(
name|ObjectMessage
operator|)
name|message
decl_stmt|;
try|try
block|{
name|Node
name|node
init|=
operator|(
name|Node
operator|)
name|objectMessage
operator|.
name|getObject
argument_list|()
decl_stmt|;
name|String
name|type
init|=
name|objectMessage
operator|.
name|getJMSType
argument_list|()
decl_stmt|;
if|if
condition|(
name|type
operator|!=
literal|null
operator|&&
name|type
operator|.
name|equals
argument_list|(
literal|"shutdown"
argument_list|)
condition|)
block|{
name|stateService
operator|.
name|shutdown
argument_list|(
name|node
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|stateService
operator|.
name|keepAlive
argument_list|(
name|node
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|log
operator|.
name|error
argument_list|(
literal|"Could not extract node from message: "
operator|+
name|e
operator|+
literal|". Message: "
operator|+
name|message
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|log
operator|.
name|warn
argument_list|(
literal|"Ignoring message: "
operator|+
name|message
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

