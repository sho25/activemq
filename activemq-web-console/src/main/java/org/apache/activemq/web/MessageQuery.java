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
package|;
end_package

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|JMSException
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|MapMessage
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
name|ObjectMessage
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|TextMessage
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Enumeration
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
import|;
end_import

begin_comment
comment|/**  * Allow the user to browse a message on a queue by its ID  *   * @version $Revision$  */
end_comment

begin_class
specifier|public
class|class
name|MessageQuery
extends|extends
name|QueueBrowseQuery
block|{
specifier|private
name|String
name|id
decl_stmt|;
specifier|private
name|Message
name|message
decl_stmt|;
specifier|public
name|MessageQuery
parameter_list|(
name|BrokerFacade
name|brokerFacade
parameter_list|,
name|SessionPool
name|sessionPool
parameter_list|)
throws|throws
name|JMSException
block|{
name|super
argument_list|(
name|brokerFacade
argument_list|,
name|sessionPool
argument_list|)
expr_stmt|;
block|}
specifier|public
name|String
name|getId
parameter_list|()
block|{
return|return
name|id
return|;
block|}
specifier|public
name|void
name|setId
parameter_list|(
name|String
name|id
parameter_list|)
block|{
name|this
operator|.
name|id
operator|=
name|id
expr_stmt|;
block|}
specifier|public
name|void
name|setMessage
parameter_list|(
name|Message
name|message
parameter_list|)
block|{
name|this
operator|.
name|message
operator|=
name|message
expr_stmt|;
block|}
specifier|public
name|Message
name|getMessage
parameter_list|()
throws|throws
name|JMSException
block|{
if|if
condition|(
name|message
operator|==
literal|null
condition|)
block|{
if|if
condition|(
name|id
operator|!=
literal|null
condition|)
block|{
name|Enumeration
name|iter
init|=
name|getBrowser
argument_list|()
operator|.
name|getEnumeration
argument_list|()
decl_stmt|;
while|while
condition|(
name|iter
operator|.
name|hasMoreElements
argument_list|()
condition|)
block|{
name|Message
name|item
init|=
operator|(
name|Message
operator|)
name|iter
operator|.
name|nextElement
argument_list|()
decl_stmt|;
if|if
condition|(
name|id
operator|.
name|equals
argument_list|(
name|item
operator|.
name|getJMSMessageID
argument_list|()
argument_list|)
condition|)
block|{
name|message
operator|=
name|item
expr_stmt|;
break|break;
block|}
block|}
block|}
block|}
return|return
name|message
return|;
block|}
specifier|public
name|Object
name|getBody
parameter_list|()
throws|throws
name|JMSException
block|{
name|Message
name|message
init|=
name|getMessage
argument_list|()
decl_stmt|;
if|if
condition|(
name|message
operator|instanceof
name|TextMessage
condition|)
block|{
return|return
operator|(
operator|(
name|TextMessage
operator|)
name|message
operator|)
operator|.
name|getText
argument_list|()
return|;
block|}
if|if
condition|(
name|message
operator|instanceof
name|ObjectMessage
condition|)
block|{
return|return
operator|(
operator|(
name|ObjectMessage
operator|)
name|message
operator|)
operator|.
name|getObject
argument_list|()
return|;
block|}
if|if
condition|(
name|message
operator|instanceof
name|MapMessage
condition|)
block|{
return|return
name|createMapBody
argument_list|(
operator|(
name|MapMessage
operator|)
name|message
argument_list|)
return|;
block|}
return|return
literal|null
return|;
block|}
specifier|public
name|Map
name|getPropertiesMap
parameter_list|()
throws|throws
name|JMSException
block|{
name|Map
name|answer
init|=
operator|new
name|HashMap
argument_list|()
decl_stmt|;
name|Message
name|aMessage
init|=
name|getMessage
argument_list|()
decl_stmt|;
name|Enumeration
name|iter
init|=
name|aMessage
operator|.
name|getPropertyNames
argument_list|()
decl_stmt|;
while|while
condition|(
name|iter
operator|.
name|hasMoreElements
argument_list|()
condition|)
block|{
name|String
name|name
init|=
operator|(
name|String
operator|)
name|iter
operator|.
name|nextElement
argument_list|()
decl_stmt|;
name|Object
name|value
init|=
name|aMessage
operator|.
name|getObjectProperty
argument_list|(
name|name
argument_list|)
decl_stmt|;
if|if
condition|(
name|value
operator|!=
literal|null
condition|)
block|{
name|answer
operator|.
name|put
argument_list|(
name|name
argument_list|,
name|value
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|answer
return|;
block|}
specifier|protected
name|Map
name|createMapBody
parameter_list|(
name|MapMessage
name|mapMessage
parameter_list|)
throws|throws
name|JMSException
block|{
name|Map
name|answer
init|=
operator|new
name|HashMap
argument_list|()
decl_stmt|;
name|Enumeration
name|iter
init|=
name|mapMessage
operator|.
name|getMapNames
argument_list|()
decl_stmt|;
while|while
condition|(
name|iter
operator|.
name|hasMoreElements
argument_list|()
condition|)
block|{
name|String
name|name
init|=
operator|(
name|String
operator|)
name|iter
operator|.
name|nextElement
argument_list|()
decl_stmt|;
name|Object
name|value
init|=
name|mapMessage
operator|.
name|getObject
argument_list|(
name|name
argument_list|)
decl_stmt|;
if|if
condition|(
name|value
operator|!=
literal|null
condition|)
block|{
name|answer
operator|.
name|put
argument_list|(
name|name
argument_list|,
name|value
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|answer
return|;
block|}
block|}
end_class

end_unit

