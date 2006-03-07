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
name|udp
package|;
end_package

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|ByteBuffer
import|;
end_import

begin_comment
comment|/**  *   * @version $Revision$  */
end_comment

begin_class
specifier|public
class|class
name|DatagramHeaderMarshaller
block|{
specifier|public
name|DatagramHeader
name|readHeader
parameter_list|(
name|ByteBuffer
name|readBuffer
parameter_list|)
block|{
name|DatagramHeader
name|answer
init|=
operator|new
name|DatagramHeader
argument_list|()
decl_stmt|;
name|answer
operator|.
name|setCounter
argument_list|(
name|readBuffer
operator|.
name|getLong
argument_list|()
argument_list|)
expr_stmt|;
name|byte
name|flags
init|=
name|readBuffer
operator|.
name|get
argument_list|()
decl_stmt|;
name|answer
operator|.
name|setFlags
argument_list|(
name|flags
argument_list|)
expr_stmt|;
return|return
name|answer
return|;
block|}
specifier|public
name|void
name|writeHeader
parameter_list|(
name|DatagramHeader
name|header
parameter_list|,
name|ByteBuffer
name|writeBuffer
parameter_list|)
block|{
name|writeBuffer
operator|.
name|putLong
argument_list|(
name|header
operator|.
name|getCounter
argument_list|()
argument_list|)
expr_stmt|;
name|writeBuffer
operator|.
name|put
argument_list|(
name|header
operator|.
name|getFlags
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|public
name|int
name|getHeaderSize
parameter_list|(
name|DatagramHeader
name|header
parameter_list|)
block|{
return|return
literal|8
operator|+
literal|1
return|;
block|}
block|}
end_class

end_unit

