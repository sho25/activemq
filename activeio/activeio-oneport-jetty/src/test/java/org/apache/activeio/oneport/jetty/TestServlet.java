begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  *  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  * http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
package|package
name|org
operator|.
name|apache
operator|.
name|activeio
operator|.
name|oneport
operator|.
name|jetty
package|;
end_package

begin_import
import|import
name|edu
operator|.
name|emory
operator|.
name|mathcs
operator|.
name|backport
operator|.
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|TimeUnit
import|;
end_import

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

begin_class
specifier|public
class|class
name|TestServlet
extends|extends
name|HttpServlet
block|{
specifier|private
specifier|static
specifier|final
name|long
name|serialVersionUID
init|=
literal|3257286933137733686L
decl_stmt|;
specifier|protected
name|void
name|service
parameter_list|(
name|HttpServletRequest
name|req
parameter_list|,
name|HttpServletResponse
name|resp
parameter_list|)
throws|throws
name|ServletException
throws|,
name|IOException
block|{
try|try
block|{
name|JettyOnePortSocketListenerTest
operator|.
name|staticResultSlot
operator|.
name|offer
argument_list|(
literal|"HTTP"
argument_list|,
literal|1
argument_list|,
name|TimeUnit
operator|.
name|MILLISECONDS
argument_list|)
expr_stmt|;
name|resp
operator|.
name|getOutputStream
argument_list|()
operator|.
name|print
argument_list|(
literal|"Hello World!"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

