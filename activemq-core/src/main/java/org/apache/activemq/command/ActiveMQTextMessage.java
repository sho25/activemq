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
name|command
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
name|ActiveMQConnection
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
name|util
operator|.
name|ByteArrayInputStream
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
name|util
operator|.
name|ByteArrayOutputStream
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
name|util
operator|.
name|ByteSequence
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
name|util
operator|.
name|JMSExceptionSupport
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
name|util
operator|.
name|MarshallingSupport
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

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|JMSException
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|MessageNotWriteableException
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|TextMessage
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|DataInputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|DataOutputStream
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
name|io
operator|.
name|InputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|OutputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|zip
operator|.
name|DeflaterOutputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|zip
operator|.
name|InflaterInputStream
import|;
end_import

begin_comment
comment|/**  * @openwire:marshaller code="28"  * @version $Revision$  */
end_comment

begin_class
specifier|public
class|class
name|ActiveMQTextMessage
extends|extends
name|ActiveMQMessage
implements|implements
name|TextMessage
block|{
specifier|public
specifier|static
specifier|final
name|byte
name|DATA_STRUCTURE_TYPE
init|=
name|CommandTypes
operator|.
name|ACTIVEMQ_TEXT_MESSAGE
decl_stmt|;
specifier|protected
name|String
name|text
decl_stmt|;
specifier|public
name|Message
name|copy
parameter_list|()
block|{
name|ActiveMQTextMessage
name|copy
init|=
operator|new
name|ActiveMQTextMessage
argument_list|()
decl_stmt|;
name|copy
argument_list|(
name|copy
argument_list|)
expr_stmt|;
return|return
name|copy
return|;
block|}
specifier|private
name|void
name|copy
parameter_list|(
name|ActiveMQTextMessage
name|copy
parameter_list|)
block|{
name|super
operator|.
name|copy
argument_list|(
name|copy
argument_list|)
expr_stmt|;
name|copy
operator|.
name|text
operator|=
name|text
expr_stmt|;
block|}
specifier|public
name|byte
name|getDataStructureType
parameter_list|()
block|{
return|return
name|DATA_STRUCTURE_TYPE
return|;
block|}
specifier|public
name|String
name|getJMSXMimeType
parameter_list|()
block|{
return|return
literal|"jms/text-message"
return|;
block|}
specifier|public
name|void
name|setText
parameter_list|(
name|String
name|text
parameter_list|)
throws|throws
name|MessageNotWriteableException
block|{
name|checkReadOnlyBody
argument_list|()
expr_stmt|;
name|this
operator|.
name|text
operator|=
name|text
expr_stmt|;
name|setContent
argument_list|(
literal|null
argument_list|)
expr_stmt|;
block|}
specifier|public
name|String
name|getText
parameter_list|()
throws|throws
name|JMSException
block|{
if|if
condition|(
name|text
operator|==
literal|null
operator|&&
name|getContent
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|InputStream
name|is
init|=
literal|null
decl_stmt|;
try|try
block|{
name|ByteSequence
name|bodyAsBytes
init|=
name|getContent
argument_list|()
decl_stmt|;
if|if
condition|(
name|bodyAsBytes
operator|!=
literal|null
condition|)
block|{
name|is
operator|=
operator|new
name|ByteArrayInputStream
argument_list|(
name|bodyAsBytes
argument_list|)
expr_stmt|;
if|if
condition|(
name|isCompressed
argument_list|()
condition|)
block|{
name|is
operator|=
operator|new
name|InflaterInputStream
argument_list|(
name|is
argument_list|)
expr_stmt|;
block|}
name|DataInputStream
name|dataIn
init|=
operator|new
name|DataInputStream
argument_list|(
name|is
argument_list|)
decl_stmt|;
name|text
operator|=
name|MarshallingSupport
operator|.
name|readUTF8
argument_list|(
name|dataIn
argument_list|)
expr_stmt|;
name|dataIn
operator|.
name|close
argument_list|()
expr_stmt|;
name|setContent
argument_list|(
literal|null
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|IOException
name|ioe
parameter_list|)
block|{
throw|throw
name|JMSExceptionSupport
operator|.
name|create
argument_list|(
name|ioe
argument_list|)
throw|;
block|}
finally|finally
block|{
if|if
condition|(
name|is
operator|!=
literal|null
condition|)
block|{
try|try
block|{
name|is
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
comment|// ignore
block|}
block|}
block|}
block|}
return|return
name|text
return|;
block|}
specifier|public
name|void
name|beforeMarshall
parameter_list|(
name|WireFormat
name|wireFormat
parameter_list|)
throws|throws
name|IOException
block|{
name|super
operator|.
name|beforeMarshall
argument_list|(
name|wireFormat
argument_list|)
expr_stmt|;
name|ByteSequence
name|content
init|=
name|getContent
argument_list|()
decl_stmt|;
if|if
condition|(
name|content
operator|==
literal|null
operator|&&
name|text
operator|!=
literal|null
condition|)
block|{
name|ByteArrayOutputStream
name|bytesOut
init|=
operator|new
name|ByteArrayOutputStream
argument_list|()
decl_stmt|;
name|OutputStream
name|os
init|=
name|bytesOut
decl_stmt|;
name|ActiveMQConnection
name|connection
init|=
name|getConnection
argument_list|()
decl_stmt|;
if|if
condition|(
name|connection
operator|!=
literal|null
operator|&&
name|connection
operator|.
name|isUseCompression
argument_list|()
condition|)
block|{
name|compressed
operator|=
literal|true
expr_stmt|;
name|os
operator|=
operator|new
name|DeflaterOutputStream
argument_list|(
name|os
argument_list|)
expr_stmt|;
block|}
name|DataOutputStream
name|dataOut
init|=
operator|new
name|DataOutputStream
argument_list|(
name|os
argument_list|)
decl_stmt|;
name|MarshallingSupport
operator|.
name|writeUTF8
argument_list|(
name|dataOut
argument_list|,
name|text
argument_list|)
expr_stmt|;
name|dataOut
operator|.
name|close
argument_list|()
expr_stmt|;
name|setContent
argument_list|(
name|bytesOut
operator|.
name|toByteSequence
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**      * Clears out the message body. Clearing a message's body does not clear its      * header values or property entries.<p/>      *<P>      * If this message body was read-only, calling this method leaves the      * message body in the same state as an empty body in a newly created      * message.      *       * @throws JMSException if the JMS provider fails to clear the message body      *                 due to some internal error.      */
specifier|public
name|void
name|clearBody
parameter_list|()
throws|throws
name|JMSException
block|{
name|super
operator|.
name|clearBody
argument_list|()
expr_stmt|;
name|this
operator|.
name|text
operator|=
literal|null
expr_stmt|;
block|}
specifier|public
name|int
name|getSize
parameter_list|()
block|{
if|if
condition|(
name|size
operator|==
literal|0
operator|&&
name|content
operator|==
literal|null
operator|&&
name|text
operator|!=
literal|null
condition|)
block|{
name|size
operator|=
name|AVERAGE_MESSAGE_SIZE_OVERHEAD
expr_stmt|;
if|if
condition|(
name|marshalledProperties
operator|!=
literal|null
condition|)
name|size
operator|+=
name|marshalledProperties
operator|.
name|getLength
argument_list|()
expr_stmt|;
name|size
operator|=
name|text
operator|.
name|length
argument_list|()
operator|*
literal|2
expr_stmt|;
block|}
return|return
name|super
operator|.
name|getSize
argument_list|()
return|;
block|}
block|}
end_class

end_unit

