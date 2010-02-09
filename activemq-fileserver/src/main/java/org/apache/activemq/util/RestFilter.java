begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *      http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_comment
comment|//========================================================================
end_comment

begin_comment
comment|//Copyright 2007 CSC - Scientific Computing Ltd.
end_comment

begin_comment
comment|//========================================================================
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
name|File
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|FileOutputStream
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
name|java
operator|.
name|net
operator|.
name|HttpURLConnection
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|URL
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
name|UnavailableException
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
name|eclipse
operator|.
name|jetty
operator|.
name|util
operator|.
name|IO
import|;
end_import

begin_import
import|import
name|org
operator|.
name|eclipse
operator|.
name|jetty
operator|.
name|util
operator|.
name|URIUtil
import|;
end_import

begin_import
import|import
name|org
operator|.
name|eclipse
operator|.
name|jetty
operator|.
name|util
operator|.
name|log
operator|.
name|Log
import|;
end_import

begin_comment
comment|/**  *<p>  * Adds support for HTTP PUT, MOVE and DELETE methods. If init parameters  * read-permission-role and write-permission-role are defined then all requests  * are authorized using the defined roles. Also GET methods are authorized.  *</p>  *   * @author Aleksi Kallio  */
end_comment

begin_class
specifier|public
class|class
name|RestFilter
implements|implements
name|Filter
block|{
specifier|private
specifier|static
specifier|final
name|String
name|HTTP_HEADER_DESTINATION
init|=
literal|"Destination"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|HTTP_METHOD_MOVE
init|=
literal|"MOVE"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|HTTP_METHOD_PUT
init|=
literal|"PUT"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|HTTP_METHOD_GET
init|=
literal|"GET"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|HTTP_METHOD_DELETE
init|=
literal|"DELETE"
decl_stmt|;
specifier|private
name|String
name|readPermissionRole
decl_stmt|;
specifier|private
name|String
name|writePermissionRole
decl_stmt|;
specifier|private
name|FilterConfig
name|filterConfig
decl_stmt|;
specifier|public
name|void
name|init
parameter_list|(
name|FilterConfig
name|filterConfig
parameter_list|)
throws|throws
name|UnavailableException
block|{
name|this
operator|.
name|filterConfig
operator|=
name|filterConfig
expr_stmt|;
name|readPermissionRole
operator|=
name|filterConfig
operator|.
name|getInitParameter
argument_list|(
literal|"read-permission-role"
argument_list|)
expr_stmt|;
name|writePermissionRole
operator|=
name|filterConfig
operator|.
name|getInitParameter
argument_list|(
literal|"write-permission-role"
argument_list|)
expr_stmt|;
block|}
specifier|private
name|File
name|locateFile
parameter_list|(
name|HttpServletRequest
name|request
parameter_list|)
block|{
return|return
operator|new
name|File
argument_list|(
name|filterConfig
operator|.
name|getServletContext
argument_list|()
operator|.
name|getRealPath
argument_list|(
name|URIUtil
operator|.
name|addPaths
argument_list|(
name|request
operator|.
name|getServletPath
argument_list|()
argument_list|,
name|request
operator|.
name|getPathInfo
argument_list|()
argument_list|)
argument_list|)
argument_list|)
return|;
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
operator|!
operator|(
name|request
operator|instanceof
name|HttpServletRequest
operator|&&
name|response
operator|instanceof
name|HttpServletResponse
operator|)
condition|)
block|{
if|if
condition|(
name|Log
operator|.
name|isDebugEnabled
argument_list|()
condition|)
block|{
name|Log
operator|.
name|debug
argument_list|(
literal|"request not HTTP, can not understand: "
operator|+
name|request
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|chain
operator|.
name|doFilter
argument_list|(
name|request
argument_list|,
name|response
argument_list|)
expr_stmt|;
return|return;
block|}
name|HttpServletRequest
name|httpRequest
init|=
operator|(
name|HttpServletRequest
operator|)
name|request
decl_stmt|;
name|HttpServletResponse
name|httpResponse
init|=
operator|(
name|HttpServletResponse
operator|)
name|response
decl_stmt|;
if|if
condition|(
name|httpRequest
operator|.
name|getMethod
argument_list|()
operator|.
name|equals
argument_list|(
name|HTTP_METHOD_MOVE
argument_list|)
condition|)
block|{
name|doMove
argument_list|(
name|httpRequest
argument_list|,
name|httpResponse
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|httpRequest
operator|.
name|getMethod
argument_list|()
operator|.
name|equals
argument_list|(
name|HTTP_METHOD_PUT
argument_list|)
condition|)
block|{
name|doPut
argument_list|(
name|httpRequest
argument_list|,
name|httpResponse
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|httpRequest
operator|.
name|getMethod
argument_list|()
operator|.
name|equals
argument_list|(
name|HTTP_METHOD_GET
argument_list|)
condition|)
block|{
if|if
condition|(
name|checkGet
argument_list|(
name|httpRequest
argument_list|,
name|httpResponse
argument_list|)
condition|)
block|{
name|chain
operator|.
name|doFilter
argument_list|(
name|httpRequest
argument_list|,
name|httpResponse
argument_list|)
expr_stmt|;
comment|// actual processing
comment|// done elsewhere
block|}
block|}
elseif|else
if|if
condition|(
name|httpRequest
operator|.
name|getMethod
argument_list|()
operator|.
name|equals
argument_list|(
name|HTTP_METHOD_DELETE
argument_list|)
condition|)
block|{
name|doDelete
argument_list|(
name|httpRequest
argument_list|,
name|httpResponse
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|chain
operator|.
name|doFilter
argument_list|(
name|httpRequest
argument_list|,
name|httpResponse
argument_list|)
expr_stmt|;
block|}
block|}
specifier|protected
name|void
name|doMove
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
if|if
condition|(
name|Log
operator|.
name|isDebugEnabled
argument_list|()
condition|)
block|{
name|Log
operator|.
name|debug
argument_list|(
literal|"RESTful file access: MOVE request for "
operator|+
name|request
operator|.
name|getRequestURI
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|writePermissionRole
operator|!=
literal|null
operator|&&
operator|!
name|request
operator|.
name|isUserInRole
argument_list|(
name|writePermissionRole
argument_list|)
condition|)
block|{
name|response
operator|.
name|sendError
argument_list|(
name|HttpURLConnection
operator|.
name|HTTP_FORBIDDEN
argument_list|)
expr_stmt|;
return|return;
block|}
name|File
name|file
init|=
name|locateFile
argument_list|(
name|request
argument_list|)
decl_stmt|;
name|String
name|destination
init|=
name|request
operator|.
name|getHeader
argument_list|(
name|HTTP_HEADER_DESTINATION
argument_list|)
decl_stmt|;
if|if
condition|(
name|destination
operator|==
literal|null
condition|)
block|{
name|response
operator|.
name|sendError
argument_list|(
name|HttpURLConnection
operator|.
name|HTTP_BAD_REQUEST
argument_list|,
literal|"Destination header not found"
argument_list|)
expr_stmt|;
return|return;
block|}
try|try
block|{
name|URL
name|destinationUrl
init|=
operator|new
name|URL
argument_list|(
name|destination
argument_list|)
decl_stmt|;
name|IO
operator|.
name|copyFile
argument_list|(
name|file
argument_list|,
operator|new
name|File
argument_list|(
name|destinationUrl
operator|.
name|getFile
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|IO
operator|.
name|delete
argument_list|(
name|file
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|response
operator|.
name|sendError
argument_list|(
name|HttpURLConnection
operator|.
name|HTTP_INTERNAL_ERROR
argument_list|)
expr_stmt|;
comment|// file
comment|// could
comment|// not
comment|// be
comment|// moved
return|return;
block|}
name|response
operator|.
name|setStatus
argument_list|(
name|HttpURLConnection
operator|.
name|HTTP_NO_CONTENT
argument_list|)
expr_stmt|;
comment|// we return no
comment|// content
block|}
specifier|protected
name|boolean
name|checkGet
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
if|if
condition|(
name|Log
operator|.
name|isDebugEnabled
argument_list|()
condition|)
block|{
name|Log
operator|.
name|debug
argument_list|(
literal|"RESTful file access: GET request for "
operator|+
name|request
operator|.
name|getRequestURI
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|readPermissionRole
operator|!=
literal|null
operator|&&
operator|!
name|request
operator|.
name|isUserInRole
argument_list|(
name|readPermissionRole
argument_list|)
condition|)
block|{
name|response
operator|.
name|sendError
argument_list|(
name|HttpURLConnection
operator|.
name|HTTP_FORBIDDEN
argument_list|)
expr_stmt|;
return|return
literal|false
return|;
block|}
else|else
block|{
return|return
literal|true
return|;
block|}
block|}
specifier|protected
name|void
name|doPut
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
if|if
condition|(
name|Log
operator|.
name|isDebugEnabled
argument_list|()
condition|)
block|{
name|Log
operator|.
name|debug
argument_list|(
literal|"RESTful file access: PUT request for "
operator|+
name|request
operator|.
name|getRequestURI
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|writePermissionRole
operator|!=
literal|null
operator|&&
operator|!
name|request
operator|.
name|isUserInRole
argument_list|(
name|writePermissionRole
argument_list|)
condition|)
block|{
name|response
operator|.
name|sendError
argument_list|(
name|HttpURLConnection
operator|.
name|HTTP_FORBIDDEN
argument_list|)
expr_stmt|;
return|return;
block|}
name|File
name|file
init|=
name|locateFile
argument_list|(
name|request
argument_list|)
decl_stmt|;
if|if
condition|(
name|file
operator|.
name|exists
argument_list|()
condition|)
block|{
name|boolean
name|success
init|=
name|file
operator|.
name|delete
argument_list|()
decl_stmt|;
comment|// replace file if it exists
if|if
condition|(
operator|!
name|success
condition|)
block|{
name|response
operator|.
name|sendError
argument_list|(
name|HttpURLConnection
operator|.
name|HTTP_INTERNAL_ERROR
argument_list|)
expr_stmt|;
comment|// file
comment|// existed
comment|// and
comment|// could
comment|// not
comment|// be
comment|// deleted
return|return;
block|}
block|}
name|FileOutputStream
name|out
init|=
operator|new
name|FileOutputStream
argument_list|(
name|file
argument_list|)
decl_stmt|;
try|try
block|{
name|IO
operator|.
name|copy
argument_list|(
name|request
operator|.
name|getInputStream
argument_list|()
argument_list|,
name|out
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|Log
operator|.
name|warn
argument_list|(
name|Log
operator|.
name|EXCEPTION
argument_list|,
name|e
argument_list|)
expr_stmt|;
comment|// is this obsolete?
name|out
operator|.
name|close
argument_list|()
expr_stmt|;
throw|throw
name|e
throw|;
block|}
name|response
operator|.
name|setStatus
argument_list|(
name|HttpURLConnection
operator|.
name|HTTP_NO_CONTENT
argument_list|)
expr_stmt|;
comment|// we return no
comment|// content
block|}
specifier|protected
name|void
name|doDelete
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
if|if
condition|(
name|Log
operator|.
name|isDebugEnabled
argument_list|()
condition|)
block|{
name|Log
operator|.
name|debug
argument_list|(
literal|"RESTful file access: DELETE request for "
operator|+
name|request
operator|.
name|getRequestURI
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|writePermissionRole
operator|!=
literal|null
operator|&&
operator|!
name|request
operator|.
name|isUserInRole
argument_list|(
name|writePermissionRole
argument_list|)
condition|)
block|{
name|response
operator|.
name|sendError
argument_list|(
name|HttpURLConnection
operator|.
name|HTTP_FORBIDDEN
argument_list|)
expr_stmt|;
return|return;
block|}
name|File
name|file
init|=
name|locateFile
argument_list|(
name|request
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|file
operator|.
name|exists
argument_list|()
condition|)
block|{
name|response
operator|.
name|sendError
argument_list|(
name|HttpURLConnection
operator|.
name|HTTP_NOT_FOUND
argument_list|)
expr_stmt|;
comment|// file not
comment|// found
return|return;
block|}
name|boolean
name|success
init|=
name|IO
operator|.
name|delete
argument_list|(
name|file
argument_list|)
decl_stmt|;
comment|// actual delete operation
if|if
condition|(
name|success
condition|)
block|{
name|response
operator|.
name|setStatus
argument_list|(
name|HttpURLConnection
operator|.
name|HTTP_NO_CONTENT
argument_list|)
expr_stmt|;
comment|// we return
comment|// no
comment|// content
block|}
else|else
block|{
name|response
operator|.
name|sendError
argument_list|(
name|HttpURLConnection
operator|.
name|HTTP_INTERNAL_ERROR
argument_list|)
expr_stmt|;
comment|// could
comment|// not
comment|// be
comment|// deleted
comment|// due
comment|// to
comment|// internal
comment|// error
block|}
block|}
specifier|public
name|void
name|destroy
parameter_list|()
block|{
comment|// nothing to destroy
block|}
block|}
end_class

end_unit

