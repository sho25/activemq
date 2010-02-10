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
name|util
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
name|HttpServletRequestWrapper
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

begin_class
specifier|public
class|class
name|FilenameGuardFilter
implements|implements
name|Filter
block|{
specifier|private
specifier|static
specifier|final
name|Log
name|LOG
init|=
name|LogFactory
operator|.
name|getLog
argument_list|(
name|FilenameGuardFilter
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|public
name|void
name|destroy
parameter_list|()
block|{
comment|// nothing to destroy
block|}
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
comment|// nothing to init
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
if|if
condition|(
name|request
operator|instanceof
name|HttpServletRequest
condition|)
block|{
name|HttpServletRequest
name|httpRequest
init|=
operator|(
name|HttpServletRequest
operator|)
name|request
decl_stmt|;
name|GuardedHttpServletRequest
name|guardedRequest
init|=
operator|new
name|GuardedHttpServletRequest
argument_list|(
name|httpRequest
argument_list|)
decl_stmt|;
name|chain
operator|.
name|doFilter
argument_list|(
name|guardedRequest
argument_list|,
name|response
argument_list|)
expr_stmt|;
block|}
else|else
block|{
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
block|}
specifier|private
specifier|static
class|class
name|GuardedHttpServletRequest
extends|extends
name|HttpServletRequestWrapper
block|{
specifier|public
name|GuardedHttpServletRequest
parameter_list|(
name|HttpServletRequest
name|httpRequest
parameter_list|)
block|{
name|super
argument_list|(
name|httpRequest
argument_list|)
expr_stmt|;
block|}
specifier|private
name|String
name|guard
parameter_list|(
name|String
name|filename
parameter_list|)
block|{
name|String
name|guarded
init|=
name|filename
operator|.
name|replace
argument_list|(
literal|":"
argument_list|,
literal|"_"
argument_list|)
decl_stmt|;
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
literal|"guarded "
operator|+
name|filename
operator|+
literal|" to "
operator|+
name|guarded
argument_list|)
expr_stmt|;
block|}
return|return
name|guarded
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|getParameter
parameter_list|(
name|String
name|name
parameter_list|)
block|{
if|if
condition|(
name|name
operator|.
name|equals
argument_list|(
literal|"Destination"
argument_list|)
condition|)
block|{
return|return
name|guard
argument_list|(
name|super
operator|.
name|getParameter
argument_list|(
name|name
argument_list|)
argument_list|)
return|;
block|}
else|else
block|{
return|return
name|super
operator|.
name|getParameter
argument_list|(
name|name
argument_list|)
return|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|String
name|getPathInfo
parameter_list|()
block|{
return|return
name|guard
argument_list|(
name|super
operator|.
name|getPathInfo
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|getPathTranslated
parameter_list|()
block|{
return|return
name|guard
argument_list|(
name|super
operator|.
name|getPathTranslated
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|getRequestURI
parameter_list|()
block|{
return|return
name|guard
argument_list|(
name|super
operator|.
name|getRequestURI
argument_list|()
argument_list|)
return|;
block|}
block|}
block|}
end_class

end_unit

