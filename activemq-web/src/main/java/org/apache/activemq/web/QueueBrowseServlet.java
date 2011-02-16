begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *      http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|Queue
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
name|jms
operator|.
name|Session
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
name|HttpServlet
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

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|util
operator|.
name|FactoryFinder
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
name|util
operator|.
name|IntrospectionSupport
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
name|view
operator|.
name|MessageRenderer
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
name|view
operator|.
name|XmlMessageRenderer
import|;
end_import

begin_comment
comment|/**  * Renders the contents of a queue using some kind of view. The URI is assumed  * to be the queue. The following parameters can be used  *<p/>  *<ul>  *<li>view - specifies the type of the view such as simple, xml, rss</li>  *<li>selector - specifies the SQL 92 selector to apply to the queue</li>  *</ul>  *  *   */
end_comment

begin_class
specifier|public
class|class
name|QueueBrowseServlet
extends|extends
name|HttpServlet
block|{
specifier|private
specifier|static
name|FactoryFinder
name|factoryFinder
init|=
operator|new
name|FactoryFinder
argument_list|(
literal|"META-INF/services/org/apache/activemq/web/view/"
argument_list|)
decl_stmt|;
comment|// Implementation methods
comment|// -------------------------------------------------------------------------
specifier|protected
name|void
name|doGet
parameter_list|(
name|HttpServletRequest
name|request
parameter_list|,
name|HttpServletResponse
name|response
parameter_list|)
throws|throws
name|ServletException
throws|,
name|IOException
block|{
try|try
block|{
name|WebClient
name|client
init|=
name|WebClient
operator|.
name|getWebClient
argument_list|(
name|request
argument_list|)
decl_stmt|;
name|Session
name|session
init|=
name|client
operator|.
name|getSession
argument_list|()
decl_stmt|;
name|Queue
name|queue
init|=
name|getQueue
argument_list|(
name|request
argument_list|,
name|session
argument_list|)
decl_stmt|;
if|if
condition|(
name|queue
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|ServletException
argument_list|(
literal|"No queue URI specified"
argument_list|)
throw|;
block|}
name|String
name|msgId
init|=
name|request
operator|.
name|getParameter
argument_list|(
literal|"msgId"
argument_list|)
decl_stmt|;
if|if
condition|(
name|msgId
operator|==
literal|null
condition|)
block|{
name|MessageRenderer
name|renderer
init|=
name|getMessageRenderer
argument_list|(
name|request
argument_list|)
decl_stmt|;
name|configureRenderer
argument_list|(
name|request
argument_list|,
name|renderer
argument_list|)
expr_stmt|;
name|String
name|selector
init|=
name|getSelector
argument_list|(
name|request
argument_list|)
decl_stmt|;
name|QueueBrowser
name|browser
init|=
name|session
operator|.
name|createBrowser
argument_list|(
name|queue
argument_list|,
name|selector
argument_list|)
decl_stmt|;
name|renderer
operator|.
name|renderMessages
argument_list|(
name|request
argument_list|,
name|response
argument_list|,
name|browser
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|XmlMessageRenderer
name|renderer
init|=
operator|new
name|XmlMessageRenderer
argument_list|()
decl_stmt|;
name|QueueBrowser
name|browser
init|=
name|session
operator|.
name|createBrowser
argument_list|(
name|queue
argument_list|,
literal|"JMSMessageID='"
operator|+
name|msgId
operator|+
literal|"'"
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|browser
operator|.
name|getEnumeration
argument_list|()
operator|.
name|hasMoreElements
argument_list|()
condition|)
block|{
name|response
operator|.
name|sendError
argument_list|(
name|HttpServletResponse
operator|.
name|SC_NOT_FOUND
argument_list|)
expr_stmt|;
return|return;
block|}
name|Message
name|message
init|=
operator|(
name|Message
operator|)
name|browser
operator|.
name|getEnumeration
argument_list|()
operator|.
name|nextElement
argument_list|()
decl_stmt|;
name|PrintWriter
name|writer
init|=
name|response
operator|.
name|getWriter
argument_list|()
decl_stmt|;
name|renderer
operator|.
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
name|writer
operator|.
name|flush
argument_list|()
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|JMSException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|ServletException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
specifier|protected
name|MessageRenderer
name|getMessageRenderer
parameter_list|(
name|HttpServletRequest
name|request
parameter_list|)
throws|throws
name|IOException
throws|,
name|ServletException
block|{
name|String
name|style
init|=
name|request
operator|.
name|getParameter
argument_list|(
literal|"view"
argument_list|)
decl_stmt|;
if|if
condition|(
name|style
operator|==
literal|null
condition|)
block|{
name|style
operator|=
literal|"simple"
expr_stmt|;
block|}
try|try
block|{
return|return
operator|(
name|MessageRenderer
operator|)
name|factoryFinder
operator|.
name|newInstance
argument_list|(
name|style
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|IllegalAccessException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|NoSuchViewStyleException
argument_list|(
name|style
argument_list|,
name|e
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
name|InstantiationException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|NoSuchViewStyleException
argument_list|(
name|style
argument_list|,
name|e
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
name|ClassNotFoundException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|NoSuchViewStyleException
argument_list|(
name|style
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
specifier|protected
name|void
name|configureRenderer
parameter_list|(
name|HttpServletRequest
name|request
parameter_list|,
name|MessageRenderer
name|renderer
parameter_list|)
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|properties
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|Enumeration
argument_list|<
name|String
argument_list|>
name|iter
init|=
name|request
operator|.
name|getParameterNames
argument_list|()
init|;
name|iter
operator|.
name|hasMoreElements
argument_list|()
condition|;
control|)
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
name|properties
operator|.
name|put
argument_list|(
name|name
argument_list|,
name|request
operator|.
name|getParameter
argument_list|(
name|name
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|IntrospectionSupport
operator|.
name|setProperties
argument_list|(
name|renderer
argument_list|,
name|properties
argument_list|)
expr_stmt|;
block|}
specifier|protected
name|String
name|getSelector
parameter_list|(
name|HttpServletRequest
name|request
parameter_list|)
block|{
return|return
name|request
operator|.
name|getParameter
argument_list|(
literal|"selector"
argument_list|)
return|;
block|}
specifier|protected
name|Queue
name|getQueue
parameter_list|(
name|HttpServletRequest
name|request
parameter_list|,
name|Session
name|session
parameter_list|)
throws|throws
name|JMSException
block|{
name|String
name|uri
init|=
name|request
operator|.
name|getPathInfo
argument_list|()
decl_stmt|;
if|if
condition|(
name|uri
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
comment|// replace URI separator with JMS destination separator
if|if
condition|(
name|uri
operator|.
name|startsWith
argument_list|(
literal|"/"
argument_list|)
condition|)
block|{
name|uri
operator|=
name|uri
operator|.
name|substring
argument_list|(
literal|1
argument_list|)
expr_stmt|;
if|if
condition|(
name|uri
operator|.
name|length
argument_list|()
operator|==
literal|0
condition|)
block|{
return|return
literal|null
return|;
block|}
block|}
name|uri
operator|=
name|uri
operator|.
name|replace
argument_list|(
literal|'/'
argument_list|,
literal|'.'
argument_list|)
expr_stmt|;
return|return
name|session
operator|.
name|createQueue
argument_list|(
name|uri
argument_list|)
return|;
block|}
block|}
end_class

end_unit

