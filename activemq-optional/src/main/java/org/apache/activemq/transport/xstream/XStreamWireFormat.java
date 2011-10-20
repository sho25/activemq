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
name|transport
operator|.
name|xstream
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
name|Reader
import|;
end_import

begin_import
import|import
name|com
operator|.
name|thoughtworks
operator|.
name|xstream
operator|.
name|XStream
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
name|command
operator|.
name|Command
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
name|command
operator|.
name|MarshallAware
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
name|command
operator|.
name|MessageDispatch
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
name|wireformat
operator|.
name|WireFormat
import|;
end_import

begin_comment
comment|/**  * A {@link WireFormat} implementation which uses the<a  * href="http://xstream.codehaus.org/>XStream</a> library to marshall commands  * onto the wire  *  *  */
end_comment

begin_class
specifier|public
class|class
name|XStreamWireFormat
extends|extends
name|TextWireFormat
block|{
specifier|private
name|XStream
name|xStream
decl_stmt|;
specifier|private
name|int
name|version
decl_stmt|;
specifier|public
name|int
name|getVersion
parameter_list|()
block|{
return|return
name|version
return|;
block|}
specifier|public
name|void
name|setVersion
parameter_list|(
name|int
name|version
parameter_list|)
block|{
name|this
operator|.
name|version
operator|=
name|version
expr_stmt|;
block|}
specifier|public
name|WireFormat
name|copy
parameter_list|()
block|{
return|return
operator|new
name|XStreamWireFormat
argument_list|()
return|;
block|}
specifier|public
name|Object
name|unmarshalText
parameter_list|(
name|String
name|text
parameter_list|)
block|{
return|return
operator|(
name|Command
operator|)
name|getXStream
argument_list|()
operator|.
name|fromXML
argument_list|(
name|text
argument_list|)
return|;
block|}
specifier|public
name|Object
name|unmarshalText
parameter_list|(
name|Reader
name|reader
parameter_list|)
block|{
return|return
operator|(
name|Command
operator|)
name|getXStream
argument_list|()
operator|.
name|fromXML
argument_list|(
name|reader
argument_list|)
return|;
block|}
specifier|public
name|String
name|marshalText
parameter_list|(
name|Object
name|command
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|command
operator|instanceof
name|MarshallAware
condition|)
block|{
operator|(
operator|(
name|MarshallAware
operator|)
name|command
operator|)
operator|.
name|beforeMarshall
argument_list|(
name|this
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|command
operator|instanceof
name|MessageDispatch
condition|)
block|{
name|MessageDispatch
name|dispatch
init|=
operator|(
name|MessageDispatch
operator|)
name|command
decl_stmt|;
if|if
condition|(
name|dispatch
operator|!=
literal|null
operator|&&
name|dispatch
operator|.
name|getMessage
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|dispatch
operator|.
name|getMessage
argument_list|()
operator|.
name|beforeMarshall
argument_list|(
name|this
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|getXStream
argument_list|()
operator|.
name|toXML
argument_list|(
name|command
argument_list|)
return|;
block|}
comment|/**      * Can this wireformat process packets of this version      *      * @param version the version number to test      * @return true if can accept the version      */
specifier|public
name|boolean
name|canProcessWireFormatVersion
parameter_list|(
name|int
name|version
parameter_list|)
block|{
return|return
literal|true
return|;
block|}
comment|/**      * @return the current version of this wire format      */
specifier|public
name|int
name|getCurrentWireFormatVersion
parameter_list|()
block|{
return|return
literal|1
return|;
block|}
comment|// Properties
comment|// -------------------------------------------------------------------------
specifier|public
name|XStream
name|getXStream
parameter_list|()
block|{
if|if
condition|(
name|xStream
operator|==
literal|null
condition|)
block|{
name|xStream
operator|=
name|createXStream
argument_list|()
expr_stmt|;
block|}
return|return
name|xStream
return|;
block|}
specifier|public
name|void
name|setXStream
parameter_list|(
name|XStream
name|xStream
parameter_list|)
block|{
name|this
operator|.
name|xStream
operator|=
name|xStream
expr_stmt|;
block|}
comment|// Implementation methods
comment|// -------------------------------------------------------------------------
specifier|protected
name|XStream
name|createXStream
parameter_list|()
block|{
return|return
operator|new
name|XStream
argument_list|()
return|;
block|}
block|}
end_class

end_unit

