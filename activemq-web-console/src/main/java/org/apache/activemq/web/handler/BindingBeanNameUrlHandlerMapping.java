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
name|handler
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Arrays
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
name|servlet
operator|.
name|HandlerExecutionChain
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
name|handler
operator|.
name|BeanNameUrlHandlerMapping
import|;
end_import

begin_comment
comment|/**  *  */
end_comment

begin_class
specifier|public
class|class
name|BindingBeanNameUrlHandlerMapping
extends|extends
name|BeanNameUrlHandlerMapping
block|{
specifier|private
specifier|static
specifier|final
specifier|transient
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|BindingBeanNameUrlHandlerMapping
operator|.
name|class
argument_list|)
decl_stmt|;
annotation|@
name|Override
specifier|protected
name|Object
name|getHandlerInternal
parameter_list|(
name|HttpServletRequest
name|request
parameter_list|)
throws|throws
name|Exception
block|{
name|Object
name|object
init|=
name|super
operator|.
name|getHandlerInternal
argument_list|(
name|request
argument_list|)
decl_stmt|;
if|if
condition|(
name|object
operator|instanceof
name|String
condition|)
block|{
name|String
name|handlerName
init|=
operator|(
name|String
operator|)
name|object
decl_stmt|;
name|object
operator|=
name|getApplicationContext
argument_list|()
operator|.
name|getBean
argument_list|(
name|handlerName
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|object
operator|instanceof
name|HandlerExecutionChain
condition|)
block|{
name|HandlerExecutionChain
name|handlerExecutionChain
init|=
operator|(
name|HandlerExecutionChain
operator|)
name|object
decl_stmt|;
name|object
operator|=
name|handlerExecutionChain
operator|.
name|getHandler
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|object
operator|!=
literal|null
condition|)
block|{
comment|// prevent CSRF attacks
if|if
condition|(
name|object
operator|instanceof
name|DestinationFacade
condition|)
block|{
comment|// check supported methods
if|if
condition|(
operator|!
name|Arrays
operator|.
name|asList
argument_list|(
operator|(
operator|(
name|DestinationFacade
operator|)
name|object
operator|)
operator|.
name|getSupportedHttpMethods
argument_list|()
argument_list|)
operator|.
name|contains
argument_list|(
name|request
operator|.
name|getMethod
argument_list|()
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
literal|"Unsupported method "
operator|+
name|request
operator|.
name|getMethod
argument_list|()
operator|+
literal|" for path "
operator|+
name|request
operator|.
name|getRequestURI
argument_list|()
argument_list|)
throw|;
block|}
comment|// check the 'secret'
if|if
condition|(
name|request
operator|.
name|getSession
argument_list|()
operator|.
name|getAttribute
argument_list|(
literal|"secret"
argument_list|)
operator|==
literal|null
operator|||
operator|!
name|request
operator|.
name|getSession
argument_list|()
operator|.
name|getAttribute
argument_list|(
literal|"secret"
argument_list|)
operator|.
name|equals
argument_list|(
name|request
operator|.
name|getParameter
argument_list|(
literal|"secret"
argument_list|)
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
literal|"Possible CSRF attack"
argument_list|)
throw|;
block|}
block|}
name|ServletRequestDataBinder
name|binder
init|=
operator|new
name|ServletRequestDataBinder
argument_list|(
name|object
argument_list|,
literal|"request"
argument_list|)
decl_stmt|;
try|try
block|{
name|binder
operator|.
name|bind
argument_list|(
name|request
argument_list|)
expr_stmt|;
name|binder
operator|.
name|setIgnoreUnknownFields
argument_list|(
literal|true
argument_list|)
expr_stmt|;
if|if
condition|(
name|LOG
operator|.
name|isDebugEnabled
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Bound POJO is now: "
operator|+
name|object
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
name|LOG
operator|.
name|warn
argument_list|(
literal|"Caught: "
operator|+
name|e
argument_list|,
name|e
argument_list|)
expr_stmt|;
throw|throw
name|e
throw|;
block|}
block|}
return|return
name|object
return|;
block|}
block|}
end_class

end_unit

