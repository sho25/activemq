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
name|web
operator|.
name|controller
package|;
end_package

begin_import
import|import
name|javax
operator|.
name|servlet
operator|.
name|http
operator|.
name|HttpServletRequest
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|servlet
operator|.
name|http
operator|.
name|HttpServletResponse
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
name|broker
operator|.
name|jmx
operator|.
name|QueueViewMBean
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
name|web
operator|.
name|BrokerFacade
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
name|web
operator|.
name|DestinationFacade
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|Logger
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|LoggerFactory
import|;
end_import

begin_import
import|import
name|org
operator|.
name|springframework
operator|.
name|web
operator|.
name|servlet
operator|.
name|ModelAndView
import|;
end_import

begin_import
import|import
name|org
operator|.
name|springframework
operator|.
name|web
operator|.
name|servlet
operator|.
name|mvc
operator|.
name|Controller
import|;
end_import

begin_comment
comment|/**  * Moves a message from one to another queue  */
end_comment

begin_class
specifier|public
class|class
name|MoveMessage
extends|extends
name|DestinationFacade
implements|implements
name|Controller
block|{
specifier|private
name|String
name|messageId
decl_stmt|;
specifier|private
name|String
name|destination
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|Logger
name|log
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|MoveMessage
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|public
name|MoveMessage
parameter_list|(
name|BrokerFacade
name|brokerFacade
parameter_list|)
block|{
name|super
argument_list|(
name|brokerFacade
argument_list|)
expr_stmt|;
block|}
specifier|public
name|ModelAndView
name|handleRequest
parameter_list|(
name|HttpServletRequest
name|request
parameter_list|,
name|HttpServletResponse
name|response
parameter_list|)
throws|throws
name|Exception
block|{
if|if
condition|(
name|messageId
operator|!=
literal|null
condition|)
block|{
name|QueueViewMBean
name|queueView
init|=
name|getQueueView
argument_list|()
decl_stmt|;
if|if
condition|(
name|queueView
operator|!=
literal|null
condition|)
block|{
name|log
operator|.
name|info
argument_list|(
literal|"Moving message "
operator|+
name|getJMSDestination
argument_list|()
operator|+
literal|"("
operator|+
name|messageId
operator|+
literal|")"
operator|+
literal|" to "
operator|+
name|destination
argument_list|)
expr_stmt|;
name|queueView
operator|.
name|moveMessageTo
argument_list|(
name|messageId
argument_list|,
name|destination
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|log
operator|.
name|warn
argument_list|(
literal|"No queue named: "
operator|+
name|getPhysicalDestinationName
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|redirectToDestinationView
argument_list|()
return|;
block|}
specifier|public
name|String
name|getMessageId
parameter_list|()
block|{
return|return
name|messageId
return|;
block|}
specifier|public
name|void
name|setMessageId
parameter_list|(
name|String
name|messageId
parameter_list|)
block|{
name|this
operator|.
name|messageId
operator|=
name|messageId
expr_stmt|;
block|}
specifier|public
name|String
name|getDestination
parameter_list|()
block|{
return|return
name|destination
return|;
block|}
specifier|public
name|void
name|setDestination
parameter_list|(
name|String
name|destination
parameter_list|)
block|{
name|this
operator|.
name|destination
operator|=
name|destination
expr_stmt|;
block|}
block|}
end_class

end_unit

