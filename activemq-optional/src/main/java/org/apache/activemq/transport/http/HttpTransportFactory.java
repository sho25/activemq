begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  *  * Copyright 2005-2006 The Apache Software Foundation  *  * Licensed under the Apache License, Version 2.0 (the "License");  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  * http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|http
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

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|activeio
operator|.
name|command
operator|.
name|WireFormat
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
name|Transport
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
name|TransportFactory
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
name|TransportServer
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
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|transport
operator|.
name|xstream
operator|.
name|XStreamWireFormat
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

begin_comment
comment|/**  * @version $Revision$  */
end_comment

begin_class
specifier|public
class|class
name|HttpTransportFactory
extends|extends
name|TransportFactory
block|{
specifier|private
specifier|static
specifier|final
name|Log
name|log
init|=
name|LogFactory
operator|.
name|getLog
argument_list|(
name|HttpTransportFactory
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|public
name|TransportServer
name|doBind
parameter_list|(
name|String
name|brokerId
parameter_list|,
name|URI
name|location
parameter_list|)
throws|throws
name|IOException
block|{
return|return
operator|new
name|HttpTransportServer
argument_list|(
name|location
argument_list|)
return|;
block|}
specifier|protected
name|TextWireFormat
name|asTextWireFormat
parameter_list|(
name|WireFormat
name|wireFormat
parameter_list|)
block|{
if|if
condition|(
name|wireFormat
operator|instanceof
name|TextWireFormat
condition|)
block|{
return|return
operator|(
name|TextWireFormat
operator|)
name|wireFormat
return|;
block|}
name|log
operator|.
name|trace
argument_list|(
literal|"Not created with a TextWireFormat: "
operator|+
name|wireFormat
argument_list|)
expr_stmt|;
return|return
operator|new
name|XStreamWireFormat
argument_list|()
return|;
block|}
specifier|protected
name|String
name|getDefaultWireFormatType
parameter_list|()
block|{
return|return
literal|"xstream"
return|;
block|}
specifier|protected
name|Transport
name|createTransport
parameter_list|(
name|URI
name|location
parameter_list|,
name|WireFormat
name|wf
parameter_list|)
throws|throws
name|MalformedURLException
block|{
name|TextWireFormat
name|textWireFormat
init|=
name|asTextWireFormat
argument_list|(
name|wf
argument_list|)
decl_stmt|;
name|Transport
name|transport
init|=
operator|new
name|HttpClientTransport
argument_list|(
name|textWireFormat
argument_list|,
name|location
argument_list|)
decl_stmt|;
return|return
name|transport
return|;
block|}
block|}
end_class

end_unit

