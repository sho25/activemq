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
name|filter
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
name|util
operator|.
name|AbstractMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collections
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
name|java
operator|.
name|util
operator|.
name|Set
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|servlet
operator|.
name|Filter
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|servlet
operator|.
name|FilterChain
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|servlet
operator|.
name|FilterConfig
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|servlet
operator|.
name|ServletContext
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
name|ServletRequest
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|servlet
operator|.
name|ServletResponse
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
name|web
operator|.
name|BrokerFacade
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
name|bind
operator|.
name|ServletRequestDataBinder
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
name|context
operator|.
name|WebApplicationContext
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
name|context
operator|.
name|support
operator|.
name|WebApplicationContextUtils
import|;
end_import

begin_comment
comment|/**  * Exposes Spring ApplicationContexts to JSP EL and other view technologies.  * Currently a variable is placed in application scope (by default called  * 'applicationContext') so that POJOs can be pulled out of Spring in a JSP page  * to render things using EL expressions.<br/>  *   * e.g. ${applicationContext.cheese} would access the cheese POJO. Or  * ${applicationContext.cheese.name} would access the name property of the  * cheese POJO.<br/>  *   * You can then use JSTL to work with these POJOs such as&lt;c.set var="myfoo"  * value="${applicationContext.foo}"/&gt;<br/>  *   * In addition to applicationContext a 'requestContext' variable is created  * which will automatically bind any request parameters to the POJOs extracted  * from the applicationContext - which is ideal for POJOs which implement  * queries in view technologies.  *   *   */
end_comment

begin_class
specifier|public
class|class
name|ApplicationContextFilter
implements|implements
name|Filter
block|{
specifier|private
name|ServletContext
name|servletContext
decl_stmt|;
specifier|private
name|String
name|applicationContextName
init|=
literal|"applicationContext"
decl_stmt|;
specifier|private
name|String
name|requestContextName
init|=
literal|"requestContext"
decl_stmt|;
specifier|private
name|String
name|requestName
init|=
literal|"request"
decl_stmt|;
specifier|private
specifier|final
name|String
name|slavePage
init|=
literal|"slave.jsp"
decl_stmt|;
specifier|public
name|void
name|init
parameter_list|(
name|FilterConfig
name|config
parameter_list|)
throws|throws
name|ServletException
block|{
name|this
operator|.
name|servletContext
operator|=
name|config
operator|.
name|getServletContext
argument_list|()
expr_stmt|;
name|this
operator|.
name|applicationContextName
operator|=
name|getInitParameter
argument_list|(
name|config
argument_list|,
literal|"applicationContextName"
argument_list|,
name|applicationContextName
argument_list|)
expr_stmt|;
name|this
operator|.
name|requestContextName
operator|=
name|getInitParameter
argument_list|(
name|config
argument_list|,
literal|"requestContextName"
argument_list|,
name|requestContextName
argument_list|)
expr_stmt|;
name|this
operator|.
name|requestName
operator|=
name|getInitParameter
argument_list|(
name|config
argument_list|,
literal|"requestName"
argument_list|,
name|requestName
argument_list|)
expr_stmt|;
comment|// register the application context in the applicationScope
name|WebApplicationContext
name|context
init|=
name|WebApplicationContextUtils
operator|.
name|getWebApplicationContext
argument_list|(
name|servletContext
argument_list|)
decl_stmt|;
name|Map
name|wrapper
init|=
name|createApplicationContextWrapper
argument_list|(
name|context
argument_list|)
decl_stmt|;
name|servletContext
operator|.
name|setAttribute
argument_list|(
name|applicationContextName
argument_list|,
name|wrapper
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|doFilter
parameter_list|(
name|ServletRequest
name|request
parameter_list|,
name|ServletResponse
name|response
parameter_list|,
name|FilterChain
name|chain
parameter_list|)
throws|throws
name|IOException
throws|,
name|ServletException
block|{
comment|// lets register a requestContext in the requestScope
name|Map
name|requestContextWrapper
init|=
name|createRequestContextWrapper
argument_list|(
name|request
argument_list|)
decl_stmt|;
name|String
name|path
init|=
operator|(
operator|(
name|HttpServletRequest
operator|)
name|request
operator|)
operator|.
name|getRequestURI
argument_list|()
decl_stmt|;
comment|// handle slave brokers
try|try
block|{
if|if
condition|(
operator|(
operator|(
name|BrokerFacade
operator|)
name|requestContextWrapper
operator|.
name|get
argument_list|(
literal|"brokerQuery"
argument_list|)
operator|)
operator|.
name|isSlave
argument_list|()
operator|&&
operator|(
operator|!
operator|(
name|path
operator|.
name|endsWith
argument_list|(
literal|"css"
argument_list|)
operator|||
name|path
operator|.
name|endsWith
argument_list|(
literal|"png"
argument_list|)
operator|)
operator|&&
operator|!
name|path
operator|.
name|endsWith
argument_list|(
name|slavePage
argument_list|)
operator|)
condition|)
block|{
operator|(
operator|(
name|HttpServletResponse
operator|)
name|response
operator|)
operator|.
name|sendRedirect
argument_list|(
name|slavePage
argument_list|)
expr_stmt|;
return|return;
block|}
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
name|e
argument_list|)
throw|;
block|}
name|request
operator|.
name|setAttribute
argument_list|(
name|requestContextName
argument_list|,
name|requestContextWrapper
argument_list|)
expr_stmt|;
name|request
operator|.
name|setAttribute
argument_list|(
name|requestName
argument_list|,
name|request
argument_list|)
expr_stmt|;
name|chain
operator|.
name|doFilter
argument_list|(
name|request
argument_list|,
name|response
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|destroy
parameter_list|()
block|{     }
specifier|public
name|ServletContext
name|getServletContext
parameter_list|()
block|{
return|return
name|servletContext
return|;
block|}
specifier|public
name|String
name|getApplicationContextName
parameter_list|()
block|{
return|return
name|applicationContextName
return|;
block|}
specifier|public
name|void
name|setApplicationContextName
parameter_list|(
name|String
name|variableName
parameter_list|)
block|{
name|this
operator|.
name|applicationContextName
operator|=
name|variableName
expr_stmt|;
block|}
specifier|public
name|String
name|getRequestContextName
parameter_list|()
block|{
return|return
name|requestContextName
return|;
block|}
specifier|public
name|void
name|setRequestContextName
parameter_list|(
name|String
name|requestContextName
parameter_list|)
block|{
name|this
operator|.
name|requestContextName
operator|=
name|requestContextName
expr_stmt|;
block|}
specifier|protected
name|String
name|getInitParameter
parameter_list|(
name|FilterConfig
name|config
parameter_list|,
name|String
name|key
parameter_list|,
name|String
name|defaultValue
parameter_list|)
block|{
name|String
name|parameter
init|=
name|config
operator|.
name|getInitParameter
argument_list|(
name|key
argument_list|)
decl_stmt|;
return|return
operator|(
name|parameter
operator|!=
literal|null
operator|)
condition|?
name|parameter
else|:
name|defaultValue
return|;
block|}
comment|/**      * Creates a wrapper around the web application context so that it can be      * accessed easily from inside JSP EL (or other expression languages in      * other view technologies).      */
specifier|protected
name|Map
name|createApplicationContextWrapper
parameter_list|(
specifier|final
name|WebApplicationContext
name|context
parameter_list|)
block|{
name|Map
name|wrapper
init|=
operator|new
name|AbstractMap
argument_list|()
block|{
specifier|public
name|WebApplicationContext
name|getContext
parameter_list|()
block|{
return|return
name|context
return|;
block|}
specifier|public
name|Object
name|get
parameter_list|(
name|Object
name|key
parameter_list|)
block|{
if|if
condition|(
name|key
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
return|return
name|context
operator|.
name|getBean
argument_list|(
name|key
operator|.
name|toString
argument_list|()
argument_list|)
return|;
block|}
specifier|public
name|Set
name|entrySet
parameter_list|()
block|{
return|return
name|Collections
operator|.
name|EMPTY_SET
return|;
block|}
block|}
decl_stmt|;
return|return
name|wrapper
return|;
block|}
comment|/**      * Creates a wrapper around the request context (e.g. to allow POJOs to be      * auto-injected from request parameter values etc) so that it can be      * accessed easily from inside JSP EL (or other expression languages in      * other view technologies).      */
specifier|protected
name|Map
name|createRequestContextWrapper
parameter_list|(
specifier|final
name|ServletRequest
name|request
parameter_list|)
block|{
specifier|final
name|WebApplicationContext
name|context
init|=
name|WebApplicationContextUtils
operator|.
name|getWebApplicationContext
argument_list|(
name|servletContext
argument_list|)
decl_stmt|;
name|Map
name|wrapper
init|=
operator|new
name|AbstractMap
argument_list|()
block|{
specifier|public
name|WebApplicationContext
name|getContext
parameter_list|()
block|{
return|return
name|context
return|;
block|}
specifier|public
name|Object
name|get
parameter_list|(
name|Object
name|key
parameter_list|)
block|{
if|if
condition|(
name|key
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
return|return
name|bindRequestBean
argument_list|(
name|context
operator|.
name|getBean
argument_list|(
name|key
operator|.
name|toString
argument_list|()
argument_list|)
argument_list|,
name|request
argument_list|)
return|;
block|}
specifier|public
name|Set
name|entrySet
parameter_list|()
block|{
return|return
name|Collections
operator|.
name|EMPTY_SET
return|;
block|}
block|}
decl_stmt|;
return|return
name|wrapper
return|;
block|}
comment|/**      * Binds properties from the request parameters to the given POJO which is      * useful for POJOs which are configurable via request parameters such as      * for query/view POJOs      */
specifier|protected
name|Object
name|bindRequestBean
parameter_list|(
name|Object
name|bean
parameter_list|,
name|ServletRequest
name|request
parameter_list|)
block|{
name|ServletRequestDataBinder
name|binder
init|=
operator|new
name|ServletRequestDataBinder
argument_list|(
name|bean
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|binder
operator|.
name|bind
argument_list|(
name|request
argument_list|)
expr_stmt|;
return|return
name|bean
return|;
block|}
block|}
end_class

end_unit

