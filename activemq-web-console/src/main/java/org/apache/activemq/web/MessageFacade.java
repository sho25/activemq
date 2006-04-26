begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  *  * Copyright 2005-2006 The Apache Software Foundation  *  * Licensed under the Apache License, Version 2.0 (the "License");  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  * http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
package|package
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|web
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
name|broker
operator|.
name|BrokerService
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
name|command
operator|.
name|ActiveMQMessage
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

begin_comment
comment|/**  *  * @version $Revision$  */
end_comment

begin_class
specifier|public
class|class
name|MessageFacade
extends|extends
name|BrokerFacade
block|{
specifier|private
name|String
name|id
decl_stmt|;
specifier|private
name|ActiveMQMessage
name|message
decl_stmt|;
specifier|public
name|MessageFacade
parameter_list|(
name|BrokerService
name|brokerService
parameter_list|)
block|{
name|super
argument_list|(
name|brokerService
argument_list|)
expr_stmt|;
block|}
specifier|public
name|ActiveMQMessage
name|getMessage
parameter_list|()
block|{
if|if
condition|(
name|message
operator|!=
literal|null
condition|)
block|{
comment|// TODO ??
block|}
return|return
name|message
return|;
block|}
block|}
end_class

end_unit

