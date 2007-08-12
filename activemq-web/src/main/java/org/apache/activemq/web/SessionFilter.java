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
name|HttpSession
import|;
end_import

begin_comment
comment|/* ------------------------------------------------------------ */
end_comment

begin_comment
comment|/**  * SessionFilter. Simple filter to ensure that all clients have a session.  * Prevents problems with AJAX requests.  */
end_comment

begin_class
specifier|public
class|class
name|SessionFilter
implements|implements
name|Filter
block|{
specifier|public
name|void
name|init
parameter_list|(
name|FilterConfig
name|filterConfig
parameter_list|)
throws|throws
name|ServletException
block|{     }
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
operator|(
operator|(
name|HttpServletRequest
operator|)
name|request
operator|)
operator|.
name|getSession
argument_list|(
literal|true
argument_list|)
expr_stmt|;
comment|// if (session.isNew())
comment|// System.err.println("NEW SESSION "+session.getId()+" for
comment|// "+((HttpServletRequest)request).getRequestURI());
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
block|}
end_class

end_unit

