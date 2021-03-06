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
name|view
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|PrintWriter
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
name|Message
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|QueueBrowser
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|servlet
operator|.
name|ServletException
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

begin_comment
comment|/**  * A simple rendering of the contents of a queue appear as a list of message  * elements which just contain an ID attribute.  *   *   */
end_comment

begin_class
specifier|public
class|class
name|SimpleMessageRenderer
implements|implements
name|MessageRenderer
block|{
specifier|private
name|String
name|contentType
init|=
literal|"text/xml"
decl_stmt|;
specifier|private
name|int
name|maxMessages
decl_stmt|;
specifier|public
name|void
name|renderMessages
parameter_list|(
name|HttpServletRequest
name|request
parameter_list|,
name|HttpServletResponse
name|response
parameter_list|,
name|QueueBrowser
name|browser
parameter_list|)
throws|throws
name|IOException
throws|,
name|JMSException
throws|,
name|ServletException
block|{
comment|// lets use XML by default
name|response
operator|.
name|setContentType
argument_list|(
name|getContentType
argument_list|()
argument_list|)
expr_stmt|;
name|PrintWriter
name|writer
init|=
name|response
operator|.
name|getWriter
argument_list|()
decl_stmt|;
name|printHeader
argument_list|(
name|writer
argument_list|,
name|browser
argument_list|,
name|request
argument_list|)
expr_stmt|;
name|Enumeration
name|iter
init|=
name|browser
operator|.
name|getEnumeration
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|counter
init|=
literal|0
init|;
name|iter
operator|.
name|hasMoreElements
argument_list|()
operator|&&
operator|(
name|maxMessages
operator|<=
literal|0
operator|||
name|counter
operator|<
name|maxMessages
operator|)
condition|;
name|counter
operator|++
control|)
block|{
name|Message
name|message
init|=
operator|(
name|Message
operator|)
name|iter
operator|.
name|nextElement
argument_list|()
decl_stmt|;
name|renderMessage
argument_list|(
name|writer
argument_list|,
name|request
argument_list|,
name|response
argument_list|,
name|browser
argument_list|,
name|message
argument_list|)
expr_stmt|;
block|}
name|printFooter
argument_list|(
name|writer
argument_list|,
name|browser
argument_list|,
name|request
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|renderMessage
parameter_list|(
name|PrintWriter
name|writer
parameter_list|,
name|HttpServletRequest
name|request
parameter_list|,
name|HttpServletResponse
name|response
parameter_list|,
name|QueueBrowser
name|browser
parameter_list|,
name|Message
name|message
parameter_list|)
throws|throws
name|JMSException
throws|,
name|ServletException
block|{
comment|// lets just write the message IDs for now
name|writer
operator|.
name|print
argument_list|(
literal|"<message id='"
argument_list|)
expr_stmt|;
name|writer
operator|.
name|print
argument_list|(
name|message
operator|.
name|getJMSMessageID
argument_list|()
argument_list|)
expr_stmt|;
name|writer
operator|.
name|println
argument_list|(
literal|"'/>"
argument_list|)
expr_stmt|;
block|}
comment|// Properties
comment|// -------------------------------------------------------------------------
specifier|public
name|int
name|getMaxMessages
parameter_list|()
block|{
return|return
name|maxMessages
return|;
block|}
specifier|public
name|void
name|setMaxMessages
parameter_list|(
name|int
name|maxMessages
parameter_list|)
block|{
name|this
operator|.
name|maxMessages
operator|=
name|maxMessages
expr_stmt|;
block|}
specifier|public
name|String
name|getContentType
parameter_list|()
block|{
return|return
name|contentType
return|;
block|}
specifier|public
name|void
name|setContentType
parameter_list|(
name|String
name|contentType
parameter_list|)
block|{
name|this
operator|.
name|contentType
operator|=
name|contentType
expr_stmt|;
block|}
comment|// Implementation methods
comment|// -------------------------------------------------------------------------
specifier|protected
name|void
name|printHeader
parameter_list|(
name|PrintWriter
name|writer
parameter_list|,
name|QueueBrowser
name|browser
parameter_list|,
name|HttpServletRequest
name|request
parameter_list|)
throws|throws
name|IOException
throws|,
name|JMSException
throws|,
name|ServletException
block|{
name|writer
operator|.
name|println
argument_list|(
literal|""
argument_list|)
expr_stmt|;
name|writer
operator|.
name|print
argument_list|(
literal|"<messages queue='"
argument_list|)
expr_stmt|;
name|writer
operator|.
name|print
argument_list|(
name|browser
operator|.
name|getQueue
argument_list|()
argument_list|)
expr_stmt|;
name|writer
operator|.
name|print
argument_list|(
literal|"'"
argument_list|)
expr_stmt|;
name|String
name|selector
init|=
name|browser
operator|.
name|getMessageSelector
argument_list|()
decl_stmt|;
if|if
condition|(
name|selector
operator|!=
literal|null
condition|)
block|{
name|writer
operator|.
name|print
argument_list|(
literal|" selector='"
argument_list|)
expr_stmt|;
name|writer
operator|.
name|print
argument_list|(
name|selector
argument_list|)
expr_stmt|;
name|writer
operator|.
name|print
argument_list|(
literal|"'"
argument_list|)
expr_stmt|;
block|}
name|writer
operator|.
name|println
argument_list|(
literal|">"
argument_list|)
expr_stmt|;
block|}
specifier|protected
name|void
name|printFooter
parameter_list|(
name|PrintWriter
name|writer
parameter_list|,
name|QueueBrowser
name|browser
parameter_list|,
name|HttpServletRequest
name|request
parameter_list|)
throws|throws
name|IOException
throws|,
name|JMSException
throws|,
name|ServletException
block|{
name|writer
operator|.
name|println
argument_list|(
literal|"</messages>"
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

