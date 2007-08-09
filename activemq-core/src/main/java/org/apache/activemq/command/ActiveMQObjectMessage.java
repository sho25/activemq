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
name|ObjectOutputStream
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
name|io
operator|.
name|Serializable
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
name|ObjectMessage
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
name|ClassLoadingAwareObjectInputStream
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

begin_comment
comment|/**  * An<CODE>ObjectMessage</CODE> object is used to send a message that  * contains a serializable object in the Java programming language ("Java  * object"). It inherits from the<CODE>Message</CODE> interface and adds a  * body containing a single reference to an object. Only  *<CODE>Serializable</CODE> Java objects can be used.<p/>  *<P>  * If a collection of Java objects must be sent, one of the  *<CODE>Collection</CODE> classes provided since JDK 1.2 can be used.<p/>  *<P>  * When a client receives an<CODE>ObjectMessage</CODE>, it is in read-only  * mode. If a client attempts to write to the message at this point, a  *<CODE>MessageNotWriteableException</CODE> is thrown. If  *<CODE>clearBody</CODE> is called, the message can now be both read from and  * written to.  *   * @openwire:marshaller code="26"  * @see javax.jms.Session#createObjectMessage()  * @see javax.jms.Session#createObjectMessage(Serializable)  * @see javax.jms.BytesMessage  * @see javax.jms.MapMessage  * @see javax.jms.Message  * @see javax.jms.StreamMessage  * @see javax.jms.TextMessage  */
end_comment

begin_class
specifier|public
class|class
name|ActiveMQObjectMessage
extends|extends
name|ActiveMQMessage
implements|implements
name|ObjectMessage
block|{
specifier|static
specifier|final
name|ClassLoader
name|ACTIVEMQ_CLASSLOADER
init|=
name|ActiveMQObjectMessage
operator|.
name|class
operator|.
name|getClassLoader
argument_list|()
decl_stmt|;
comment|// TODO
comment|// verify
comment|// classloader
specifier|public
specifier|static
specifier|final
name|byte
name|DATA_STRUCTURE_TYPE
init|=
name|CommandTypes
operator|.
name|ACTIVEMQ_OBJECT_MESSAGE
decl_stmt|;
specifier|protected
specifier|transient
name|Serializable
name|object
decl_stmt|;
specifier|public
name|Message
name|copy
parameter_list|()
block|{
name|ActiveMQObjectMessage
name|copy
init|=
operator|new
name|ActiveMQObjectMessage
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
name|ActiveMQObjectMessage
name|copy
parameter_list|)
block|{
name|storeContent
argument_list|()
expr_stmt|;
name|super
operator|.
name|copy
argument_list|(
name|copy
argument_list|)
expr_stmt|;
name|copy
operator|.
name|object
operator|=
literal|null
expr_stmt|;
block|}
specifier|public
name|void
name|storeContent
parameter_list|()
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
operator|==
literal|null
operator|&&
name|object
operator|!=
literal|null
condition|)
block|{
try|try
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
name|ObjectOutputStream
name|objOut
init|=
operator|new
name|ObjectOutputStream
argument_list|(
name|dataOut
argument_list|)
decl_stmt|;
name|objOut
operator|.
name|writeObject
argument_list|(
name|object
argument_list|)
expr_stmt|;
name|objOut
operator|.
name|flush
argument_list|()
expr_stmt|;
name|objOut
operator|.
name|reset
argument_list|()
expr_stmt|;
name|objOut
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
catch|catch
parameter_list|(
name|IOException
name|ioe
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|ioe
operator|.
name|getMessage
argument_list|()
argument_list|,
name|ioe
argument_list|)
throw|;
block|}
block|}
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
literal|"jms/object-message"
return|;
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
name|object
operator|=
literal|null
expr_stmt|;
block|}
comment|/**      * Sets the serializable object containing this message's data. It is      * important to note that an<CODE>ObjectMessage</CODE> contains a      * snapshot of the object at the time<CODE>setObject()</CODE> is called;      * subsequent modifications of the object will have no effect on the      *<CODE>ObjectMessage</CODE> body.      *       * @param newObject the message's data      * @throws JMSException if the JMS provider fails to set the object due to      *                 some internal error.      * @throws javax.jms.MessageFormatException if object serialization fails.      * @throws javax.jms.MessageNotWriteableException if the message is in      *                 read-only mode.      */
specifier|public
name|void
name|setObject
parameter_list|(
name|Serializable
name|newObject
parameter_list|)
throws|throws
name|JMSException
block|{
name|checkReadOnlyBody
argument_list|()
expr_stmt|;
name|this
operator|.
name|object
operator|=
name|newObject
expr_stmt|;
name|setContent
argument_list|(
literal|null
argument_list|)
expr_stmt|;
name|ActiveMQConnection
name|connection
init|=
name|getConnection
argument_list|()
decl_stmt|;
if|if
condition|(
name|connection
operator|==
literal|null
operator|||
operator|!
name|connection
operator|.
name|isObjectMessageSerializationDefered
argument_list|()
condition|)
block|{
name|storeContent
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**      * Gets the serializable object containing this message's data. The default      * value is null.      *       * @return the serializable object containing this message's data      * @throws JMSException      */
specifier|public
name|Serializable
name|getObject
parameter_list|()
throws|throws
name|JMSException
block|{
if|if
condition|(
name|object
operator|==
literal|null
operator|&&
name|getContent
argument_list|()
operator|!=
literal|null
condition|)
block|{
try|try
block|{
name|ByteSequence
name|content
init|=
name|getContent
argument_list|()
decl_stmt|;
name|InputStream
name|is
init|=
operator|new
name|ByteArrayInputStream
argument_list|(
name|content
argument_list|)
decl_stmt|;
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
name|ClassLoadingAwareObjectInputStream
name|objIn
init|=
operator|new
name|ClassLoadingAwareObjectInputStream
argument_list|(
name|dataIn
argument_list|)
decl_stmt|;
try|try
block|{
name|object
operator|=
operator|(
name|Serializable
operator|)
name|objIn
operator|.
name|readObject
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ClassNotFoundException
name|ce
parameter_list|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
name|ce
operator|.
name|getMessage
argument_list|()
argument_list|)
throw|;
block|}
name|dataIn
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
throw|throw
name|JMSExceptionSupport
operator|.
name|create
argument_list|(
literal|"Failed to build body from bytes. Reason: "
operator|+
name|e
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
return|return
name|this
operator|.
name|object
return|;
block|}
specifier|public
name|void
name|onMessageRolledBack
parameter_list|()
block|{
name|super
operator|.
name|onMessageRolledBack
argument_list|()
expr_stmt|;
comment|// lets force the object to be deserialized again - as we could have
comment|// changed the object
name|object
operator|=
literal|null
expr_stmt|;
block|}
specifier|public
name|String
name|toString
parameter_list|()
block|{
try|try
block|{
name|getObject
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|JMSException
name|e
parameter_list|)
block|{         }
return|return
name|super
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
end_class

end_unit

