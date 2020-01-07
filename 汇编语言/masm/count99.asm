;100% working :)
;count from 00 to 99 in decimal


.model small
.stack 64h
.data
	no db 13,10
	x db ?
	y db ?,'$'
.code
	mov ax,@data
	mov ds,ax
	mov x,30h

	up1: mov y,30h
		 
	up2: lea dx,no
		 mov ah,09h
		 int 21h
		 mov bx,0fffh
	
	d1: mov cx,0ffh
	del: loop del
		 dec bx
		 jnz d1
		 inc y
		 cmp y,03ah
		 jl up2
		 inc x
		 cmp x,03ah
		 jl up1
		 mov ah,04ch
		 int 21h
end
