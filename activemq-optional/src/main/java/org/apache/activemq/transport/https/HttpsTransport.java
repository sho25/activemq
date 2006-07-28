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
name|activemq
operator|.
name|transport
operator|.
name|https
package|;
end_package

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|transport
operator|.
name|http
operator|.
name|HttpTransport
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
name|transport
operator|.
name|util
operator|.
name|TextWireFormat
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|net
operator|.
name|ssl
operator|.
name|HttpsURLConnection
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
name|MalformedURLException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|URI
import|;
end_import

begin_class
specifier|public
class|class
name|HttpsTransport
extends|extends
name|HttpTransport
block|{
specifier|public
name|HttpsTransport
parameter_list|(
name|TextWireFormat
name|wireFormat
parameter_list|,
name|URI
name|remoteUrl
parameter_list|)
throws|throws
name|MalformedURLException
block|{
name|super
argument_list|(
name|wireFormat
argument_list|,
name|remoteUrl
argument_list|)
expr_stmt|;
block|}
specifier|protected
specifier|synchronized
name|HttpURLConnection
name|createSendConnection
parameter_list|()
throws|throws
name|IOException
block|{
name|HttpsURLConnection
name|conn
init|=
operator|(
name|HttpsURLConnection
operator|)
name|getRemoteURL
argument_list|()
operator|.
name|openConnection
argument_list|()
decl_stmt|;
name|conn
operator|.
name|setDoOutput
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|conn
operator|.
name|setRequestMethod
argument_list|(
literal|"POST"
argument_list|)
expr_stmt|;
name|configureConnection
argument_list|(
name|conn
argument_list|)
expr_stmt|;
name|conn
operator|.
name|connect
argument_list|()
expr_stmt|;
return|return
name|conn
return|;
block|}
specifier|protected
specifier|synchronized
name|HttpURLConnection
name|createReceiveConnection
parameter_list|()
throws|throws
name|IOException
block|{
name|HttpsURLConnection
name|conn
init|=
operator|(
name|HttpsURLConnection
operator|)
name|getRemoteURL
argument_list|()
operator|.
name|openConnection
argument_list|()
decl_stmt|;
name|conn
operator|.
name|setDoOutput
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|conn
operator|.
name|setDoInput
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|conn
operator|.
name|setRequestMethod
argument_list|(
literal|"GET"
argument_list|)
expr_stmt|;
name|configureConnection
argument_list|(
name|conn
argument_list|)
expr_stmt|;
name|conn
operator|.
name|connect
argument_list|()
expr_stmt|;
return|return
name|conn
return|;
block|}
block|}
end_class

end_unit

