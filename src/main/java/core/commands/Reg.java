package core.commands;

import core.common.KeysReader;
import core.common.UserInfoReader;
import core.modules.UsersDB;

import java.sql.SQLException;
import java.util.Map;

/**
 * @version 1.0
 * @author Arthur Kupriyanov
 */
public class Reg extends Command {
    public static void main(String[] args) {
        try {
            UsersDB usersDB = new UsersDB();
            System.out.println(usersDB.getFullNameByVKID(255396611));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    @Override
    public String init(String... args) {
        Map<String, String> keysMap = KeysReader.readKeys(args);

        int vkid = Integer.parseInt(UserInfoReader.readUserID(args));
        String name = UserInfoReader.readUserFirstName(args);
        String lastname = UserInfoReader.readUserLastName(args);
        String login = null;
        String password = null;
        String group;

        if (keysMap.containsKey("-g")){
            group = keysMap.get("-g");
        } else return "Укажите вашу группу с ключом -g";
        if (keysMap.containsKey("-l") && keysMap.containsKey("-p")) {
            login = keysMap.get("-l");
            password = keysMap.get("-p");
        } else if(keysMap.containsKey("-l") || keysMap.containsKey("-p")){
            return "Укажите оба ключа -l (login) и -p (password)";
        }

        try {
            UsersDB usersDB = new UsersDB();
            if (usersDB.checkUserExsist(vkid)){
                    if (login != null && password !=null){
                        usersDB.updateUserLoginPassword(login, password, vkid);
                        return "Ваши данные были обновлены";
                    }
                return "Вы уже зарегестрированы под именем " + usersDB.getFullNameByVKID(vkid);
            }
            usersDB.addUser(name, lastname,vkid,group);
            if (login != null && password !=null){
                usersDB.updateUserLoginPassword(login, password, vkid);
            }
            usersDB.closeConnection();
        } catch (SQLException e) {
            e.printStackTrace();
            return "Ошибка при работе с базой данных: " + e.toString();
        }

        return "Вы успешно добавлены в базу данных";

    }

    @Override
    protected void setName() {
        this.commandName = "reg";
    }
}
