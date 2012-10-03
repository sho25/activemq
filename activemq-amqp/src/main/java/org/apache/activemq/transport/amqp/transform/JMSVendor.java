begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
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
name|amqp
operator|.
name|transform
package|;
end_package

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|*
import|;
end_import

begin_comment
comment|/**  * @author<a href="http://hiramchirino.com">Hiram Chirino</a>  */
end_comment

begin_class
specifier|abstract
specifier|public
class|class
name|JMSVendor
block|{
specifier|public
specifier|abstract
name|BytesMessage
name|createBytesMessage
parameter_list|()
function_decl|;
specifier|public
specifier|abstract
name|StreamMessage
name|createStreamMessage
parameter_list|()
function_decl|;
specifier|public
specifier|abstract
name|Message
name|createMessage
parameter_list|()
function_decl|;
specifier|public
specifier|abstract
name|TextMessage
name|createTextMessage
parameter_list|()
function_decl|;
specifier|public
specifier|abstract
name|ObjectMessage
name|createObjectMessage
parameter_list|()
function_decl|;
specifier|public
specifier|abstract
name|MapMessage
name|createMapMessage
parameter_list|()
function_decl|;
specifier|public
specifier|abstract
name|void
name|setJMSXUserID
parameter_list|(
name|Message
name|jms
parameter_list|,
name|String
name|value
parameter_list|)
function_decl|;
specifier|public
specifier|abstract
name|Destination
name|createDestination
parameter_list|(
name|String
name|name
parameter_list|)
function_decl|;
specifier|public
specifier|abstract
name|void
name|setJMSXGroupID
parameter_list|(
name|Message
name|jms
parameter_list|,
name|String
name|groupId
parameter_list|)
function_decl|;
specifier|public
specifier|abstract
name|void
name|setJMSXGroupSequence
parameter_list|(
name|Message
name|jms
parameter_list|,
name|int
name|i
parameter_list|)
function_decl|;
block|}
end_class

end_unit

