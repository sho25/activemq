begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/** *<a href="http://activemq.org">ActiveMQ: The Open Source Message Fabric</a> * * Copyright 2005 (C) LogicBlaze, Inc. http://www.logicblaze.com * * Licensed under the Apache License, Version 2.0 (the "License"); * you may not use this file except in compliance with the License. * You may obtain a copy of the License at * * http://www.apache.org/licenses/LICENSE-2.0 * * Unless required by applicable law or agreed to in writing, software * distributed under the License is distributed on an "AS IS" BASIS, * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. * See the License for the specific language governing permissions and * limitations under the License. * **/
end_comment

begin_package
package|package
name|org
operator|.
name|activemq
operator|.
name|util
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
name|MessageEOFException
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|MessageFormatException
import|;
end_import

begin_class
specifier|final
specifier|public
class|class
name|JMSExceptionSupport
block|{
specifier|public
specifier|static
name|JMSException
name|create
parameter_list|(
name|String
name|msg
parameter_list|,
name|Throwable
name|cause
parameter_list|)
block|{
name|JMSException
name|exception
init|=
operator|new
name|JMSException
argument_list|(
name|msg
argument_list|)
decl_stmt|;
name|exception
operator|.
name|initCause
argument_list|(
name|cause
argument_list|)
expr_stmt|;
return|return
name|exception
return|;
block|}
specifier|public
specifier|static
name|JMSException
name|create
parameter_list|(
name|String
name|msg
parameter_list|,
name|Exception
name|cause
parameter_list|)
block|{
name|JMSException
name|exception
init|=
operator|new
name|JMSException
argument_list|(
name|msg
argument_list|)
decl_stmt|;
name|exception
operator|.
name|setLinkedException
argument_list|(
name|cause
argument_list|)
expr_stmt|;
name|exception
operator|.
name|initCause
argument_list|(
name|cause
argument_list|)
expr_stmt|;
return|return
name|exception
return|;
block|}
specifier|public
specifier|static
name|JMSException
name|create
parameter_list|(
name|Throwable
name|cause
parameter_list|)
block|{
if|if
condition|(
name|cause
operator|instanceof
name|JMSException
condition|)
block|{
return|return
operator|(
name|JMSException
operator|)
name|cause
return|;
block|}
name|String
name|msg
init|=
name|cause
operator|.
name|getMessage
argument_list|()
decl_stmt|;
if|if
condition|(
name|msg
operator|==
literal|null
operator|||
name|msg
operator|.
name|length
argument_list|()
operator|==
literal|0
condition|)
name|msg
operator|=
name|cause
operator|.
name|toString
argument_list|()
expr_stmt|;
name|JMSException
name|exception
init|=
operator|new
name|JMSException
argument_list|(
name|msg
argument_list|)
decl_stmt|;
name|exception
operator|.
name|initCause
argument_list|(
name|cause
argument_list|)
expr_stmt|;
return|return
name|exception
return|;
block|}
specifier|public
specifier|static
name|JMSException
name|create
parameter_list|(
name|Exception
name|cause
parameter_list|)
block|{
if|if
condition|(
name|cause
operator|instanceof
name|JMSException
condition|)
block|{
return|return
operator|(
name|JMSException
operator|)
name|cause
return|;
block|}
name|String
name|msg
init|=
name|cause
operator|.
name|getMessage
argument_list|()
decl_stmt|;
if|if
condition|(
name|msg
operator|==
literal|null
operator|||
name|msg
operator|.
name|length
argument_list|()
operator|==
literal|0
condition|)
name|msg
operator|=
name|cause
operator|.
name|toString
argument_list|()
expr_stmt|;
name|JMSException
name|exception
init|=
operator|new
name|JMSException
argument_list|(
name|msg
argument_list|)
decl_stmt|;
name|exception
operator|.
name|setLinkedException
argument_list|(
name|cause
argument_list|)
expr_stmt|;
name|exception
operator|.
name|initCause
argument_list|(
name|cause
argument_list|)
expr_stmt|;
return|return
name|exception
return|;
block|}
specifier|public
specifier|static
name|MessageEOFException
name|createMessageEOFException
parameter_list|(
name|Exception
name|cause
parameter_list|)
block|{
name|String
name|msg
init|=
name|cause
operator|.
name|getMessage
argument_list|()
decl_stmt|;
if|if
condition|(
name|msg
operator|==
literal|null
operator|||
name|msg
operator|.
name|length
argument_list|()
operator|==
literal|0
condition|)
name|msg
operator|=
name|cause
operator|.
name|toString
argument_list|()
expr_stmt|;
name|MessageEOFException
name|exception
init|=
operator|new
name|MessageEOFException
argument_list|(
name|msg
argument_list|)
decl_stmt|;
name|exception
operator|.
name|setLinkedException
argument_list|(
name|cause
argument_list|)
expr_stmt|;
name|exception
operator|.
name|initCause
argument_list|(
name|cause
argument_list|)
expr_stmt|;
return|return
name|exception
return|;
block|}
specifier|public
specifier|static
name|MessageFormatException
name|createMessageFormatException
parameter_list|(
name|Exception
name|cause
parameter_list|)
block|{
name|String
name|msg
init|=
name|cause
operator|.
name|getMessage
argument_list|()
decl_stmt|;
if|if
condition|(
name|msg
operator|==
literal|null
operator|||
name|msg
operator|.
name|length
argument_list|()
operator|==
literal|0
condition|)
name|msg
operator|=
name|cause
operator|.
name|toString
argument_list|()
expr_stmt|;
name|MessageFormatException
name|exception
init|=
operator|new
name|MessageFormatException
argument_list|(
name|msg
argument_list|)
decl_stmt|;
name|exception
operator|.
name|setLinkedException
argument_list|(
name|cause
argument_list|)
expr_stmt|;
name|exception
operator|.
name|initCause
argument_list|(
name|cause
argument_list|)
expr_stmt|;
return|return
name|exception
return|;
block|}
block|}
end_class

end_unit

